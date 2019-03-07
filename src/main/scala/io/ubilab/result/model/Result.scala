package io.ubilab.result.model

import java.util.Date

import scala.collection.mutable.ListBuffer

case class ResultId(idResult: Int)

sealed trait EventResult {
  def idOwner: Int
  def createdAt: Date
}
sealed trait SeenStateEvent extends EventResult
final case class Created(idOwner: Int, createdAt: Date) extends EventResult
final case class Received(idOwner: Int, createdAt: Date) extends EventResult
final case class Seen(idOwner: Int, createdAt: Date) extends EventResult with SeenStateEvent
final case class Unseen(idOwner: Int, createdAt: Date) extends EventResult with SeenStateEvent

case class Result(id:              Int,
                  idOwner:         Int,
                  idRecipients:    List[Int],
                  contentOfResult: String,
                  var received:    Option[Received] = None) {
  val             created = Created(idOwner, new java.util.Date())
  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  // ListBuffer created as default value for an arg in case class' constructor :
  //              objects share the same instance !
  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  private val     _seenStateEvents = ListBuffer[SeenStateEvent]()
  def             seenStateEvents: ListBuffer[SeenStateEvent] = _seenStateEvents
  def             events:          List[EventResult] = (received match {
    case Some(received) => List[EventResult](created, received)
    case None => List[EventResult](created)
  }) ++ seenStateEvents.toList.asInstanceOf[List[EventResult]]
  def             isSeen:          Boolean = seenStateEvents.lastOption match {
    case Some(event)  => event.isInstanceOf[Seen]
    case None => false
  }
}
