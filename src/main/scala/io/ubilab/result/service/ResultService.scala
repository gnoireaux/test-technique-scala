package io.ubilab.result.service

import io.ubilab.result.model._

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class Logger {
  def error(msg: String): Unit = println(s"[service.error] $msg")
}
class ResultService {
  private val results_store = ListBuffer[Result]()
  private val logger        = new Logger

  def addResult(result: Result): Try[ListBuffer[Result]] = Try {
    if (results_store.exists(_.id == result.id)) {
      throw new IllegalArgumentException("A result already exists with the same id.")
    } else {
      results_store += result
    }
  }

  def seenResult(result_id: ResultId, viewerId: ViewerId): Try[Unit] =
    findResultAndRecordEvent(result_id, Seen(viewerId.id))
  private[service] def seenResult(result: Result, viewerId: ViewerId): Try[Unit] =
    seenResult(ResultId(result.id), viewerId)

  def unseenResult(result_id: ResultId, viewerId: ViewerId): Try[Unit] =
    findResultAndRecordEvent(result_id, Unseen(viewerId.id))
  private[service] def unseenResult(result: Result, viewerId: ViewerId): Try[Unit] =
    unseenResult(ResultId(result.id), viewerId)

  private def findResultAndRecordEvent(resultId: ResultId, event: SeenStateEvent): Try[Unit] = {
    results_store.find(_.id == resultId.id) match {
      case Some(result) =>
        result.recordViewEvent(event) match {
          case f @ Failure(e) => logger.error(e.getMessage); f
          case s @ Success(_) => s
        }
      case None =>
        val e = new IllegalArgumentException(
          s"Did not find result $resultId for ${event.idOwner}. Was requesting $event.")
        logger.error(e.getMessage)
        Failure(e)
    }
  }

  def getAllResult: List[Result] = results_store.sortBy(_.created.createdAt).toList

  // Result is built to always have at least one event. (last is ugly, lastOption would be safer)
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  def getAllResultsLastModified: List[Result] = results_store.sortBy(_.events.last.createdAt).toList

  def getAllResultSeen: List[Result] =
    results_store.filter(_.isSeen).toList

  def getAllResultUnSeen: List[Result] =
    results_store.filter(!_.isSeen).toList

  def numberOfEventSeen: Int =
    results_store.map(_.seenStateEvents.count(_.isInstanceOf[Seen])).sum

  def numberOfPeopleSeen(resultId: ResultId): Int = {
    results_store.find(_.id == resultId.id).map(_.numberOfPeopleSeen).getOrElse(0)
  }
}

object ResultService {

  def build: ResultService = new ResultService
}
