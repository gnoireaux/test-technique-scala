package io.ubilab.result.model

import java.util.Date

case class EventResult(
    id:        String, // created | received | seen | unseen
    idOwner:   Int,
    createdAt: Date
)

case class Result(id:              Int,
                  idOwner:         Int,
                  idRecipients:    List[Int],
                  var isSeen:          Boolean, // var hurts
                  eventResults:    List[EventResult],
                  contentOfResult: String)
