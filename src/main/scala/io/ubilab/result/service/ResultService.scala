package io.ubilab.result.service

import io.ubilab.result.model._

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class ResultService {
  private val results_store = ListBuffer[Result]()

  def addResult(result:Result): Try[ListBuffer[Result]] = Try {
    results_store.exists(_.id == result.id) match {
      case true => throw new IllegalArgumentException("A result already exists with the same id.")
      case false => results_store += result
    }
  }

  def seenResult(result: Result, viewerId: ViewerId): Try[Unit] = seenResult(ResultId(result.id), viewerId)
  def seenResult(result_id: ResultId, viewerId: ViewerId): Try[Unit] = Try {
    results_store.find(_.id==result_id.id)
      .foreach(
        result => result.idRecipients.find(_ == viewerId.id) match {
          case Some(_) => result.seenStateEvents += Seen(viewerId.id)
          case None => {
            println(s"Viewer ${viewerId} tried to access result $result while not being a recipient.")
            throw new IllegalArgumentException(s"Viewer $viewerId attempted to see $result while not being among recipients.")
          }
        }
      )
  }

  def unseenResult(result_id:ResultId, viewerId: ViewerId): Try[Unit] =
    results_store.find(_.id==result_id.id) match {
      case Some(result) => result.unseenBy(viewerId)
      case None => throw new IllegalArgumentException(s"Did not find result with $result_id.")
    }

  def getAllResult:List[Result] = results_store.sortBy(_.created.createdAt).toList

  def getAllResultsLastModified:List[Result] = results_store.sortBy(_.events.last.createdAt).toList

  def getAllResultSeen:List[Result] =
    results_store.filter(_.isSeen).toList

  def getAllResultUnSeen:List[Result] =
    results_store.filter(!_.isSeen).toList

  def numberOfEventSeen:Int =
    results_store.map(_.seenStateEvents.count(_.isInstanceOf[Seen])).sum

  def numberOfPeopleSeen(resultId: ResultId):Int = {
    results_store.find(_.id==resultId.id).map(_.numberOfPeopleSeen).getOrElse(0)
  }
}

object ResultService {

  def build:ResultService = new ResultService
}