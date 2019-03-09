package io.ubilab.result.model

import java.util.Date

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

case class ResultId(id: Int)
object ResultId {
  def apply(result: Result): ResultId = ResultId(result.id)
}
case class ViewerId(id: Int) // would probably not exist in a real system, but I've got to stop somewhere, scope-wise

sealed trait EventResult {
  def idOwner: Int
  def createdAt: Date
}
sealed trait SeenStateEvent extends EventResult
final case class Created(idOwner:  Int, createdAt: Date = new Date()) extends EventResult
final case class Received(idOwner: Int, createdAt: Date = new Date()) extends EventResult
final case class Seen(idOwner:     Int, createdAt: Date = new Date()) extends EventResult with SeenStateEvent
final case class Unseen(idOwner:   Int, createdAt: Date = new Date()) extends EventResult with SeenStateEvent

case class Result(id:              Int,
                  idOwner:         Int,
                  idRecipients:    List[Int],
                  contentOfResult: String,
                  var received:    Option[Received] = None) {
  val created = Created(idOwner)

  private val _seenStateEvents              = ListBuffer[SeenStateEvent]()
  def seenStateEvents: List[SeenStateEvent] = _seenStateEvents.toList

  def events: List[EventResult] =
    List[Option[EventResult]](Some(created), received).flatten ++ seenStateEvents

  def isSeen: Boolean = Result.endsInASeen(seenStateEvents)

  def numberOfPeopleSeen: Int =
    seenStateEvents.groupBy(_.idOwner).count(x => Result.endsInASeen(x._2))

  def seenBy(viewerId: ViewerId): Try[Unit] = recordViewEvent(Seen(viewerId.id))

  def unseenBy(viewerId: ViewerId): Try[Unit] = recordViewEvent(Unseen(viewerId.id))

  def recordViewEvent(event: SeenStateEvent): Try[Unit] = {
    if (idRecipients.contains(event.idOwner))
      Success(_seenStateEvents += event)
    else
      Failure(
        new IllegalArgumentException(
          s"Viewer ${event.idOwner} attempted to $event $this while not being among recipients."))
  }
}
object Result {
  def endsInASeen(seenStateEvents: List[SeenStateEvent]): Boolean = seenStateEvents.lastOption match {
    case Some(event) => event.isInstanceOf[Seen]
    case None        => false
  }
}
