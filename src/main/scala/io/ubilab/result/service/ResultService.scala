package io.ubilab.result.service

import io.ubilab.result.model._

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class Logger {
  def error(msg:String) = println(msg)
}
class ResultService {
  private val results_store = ListBuffer[Result]()
  private val logger        = new Logger

  def addResult(result:Result): Try[ListBuffer[Result]] = Try {
    results_store.exists(_.id == result.id) match {
      case true => throw new IllegalArgumentException("A result already exists with the same id.")
      case false => results_store += result
    }
  }

  private[service] def seenResult(result: Result, viewerId: ViewerId): Try[Unit] =
    seenResult(ResultId(result.id), viewerId)
  def seenResult(result_id: ResultId, viewerId: ViewerId): Try[Unit] =
    findResultAndRecordEvent(result_id, Seen(viewerId.id))

  private[service] def unseenResult(result: Result, viewerId: ViewerId): Try[Unit] =
    unseenResult(ResultId(result.id), viewerId)
  def unseenResult(result_id:ResultId, viewerId: ViewerId): Try[Unit] =
    findResultAndRecordEvent(result_id, Unseen(viewerId.id))

  private def findResultAndRecordEvent(resultId: ResultId, event: SeenStateEvent): Try[Unit] = {
    results_store.find(_.id==resultId.id) match {
      case Some(result) => result.recordViewEvent(event)
      case None => {
        val e = new IllegalArgumentException(s"Did not find result $resultId for ${event.idOwner}. Was requesting $event.")
        logger.error(e.getMessage)
        Failure(e)
      }
    }
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