package io.ubilab.result.model

import java.util.Date


sealed trait EventResult
sealed trait SeenState
final case class Created(idOwner: Int, createdAt: Date) extends EventResult
final case class Received(idOwner: Int, createdAt: Date) extends EventResult
final case class Seen(idOwner: Int, createdAt: Date) extends EventResult with SeenState
final case class Unseen(idOwner: Int, createdAt: Date) extends EventResult with SeenState

case class Result(id:              Int,
                  idOwner:         Int,
                  idRecipients:    List[Int],
                  var isSeen:          Boolean, // var hurts
                  contentOfResult: String,
                  created:         Created = Created(0, new java.util.Date(System.currentTimeMillis())),
                  var received:    Option[Received] = None,
                  seenState:       List[SeenState] = List.empty) {
  def             events:          List[EventResult] = (received match {
    case Some(received) => List[EventResult](created, received)
    case None => List[EventResult](created)
  }) ++ seenState.asInstanceOf[List[EventResult]]
}
