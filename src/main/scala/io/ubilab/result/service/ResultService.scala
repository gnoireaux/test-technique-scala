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

  def seenResult(result: Result): Unit = seenResult(ResultId(result.id))
  def seenResult(result_id: ResultId): Unit =
    results_store.find(_.id==result_id.idResult).foreach(_.seenStateEvents += Seen(0, new java.util.Date()))

  def unseenResult(idResult:Int) =
    results_store.find(_.id==idResult).foreach(_.seenStateEvents += Unseen(0, new java.util.Date()))

  def getAllResult:List[Result] = results_store.sortBy(_.created.createdAt).toList

  def getAllResultsLastModified:List[Result] = results_store.sortBy(_.events.last.createdAt).toList

  def getAllResultSeen:List[Result] =
    results_store.filter(_.isSeen).toList

  def getAllResultUnSeen:List[Result] =
    results_store.filter(!_.isSeen).toList

  def numberOfEventSeen:Int =
    results_store.map(_.seenStateEvents.count(_.isInstanceOf[Seen])).sum
}

object ResultService {

  def build:ResultService = new ResultService
}