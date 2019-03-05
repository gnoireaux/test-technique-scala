package io.ubilab.result.service

import io.ubilab.result.model.Result

import scala.collection.mutable.ListBuffer

class ResultService {
  private val results_store = ListBuffer[Result]()

  def addResult(result:Result) = results_store += result


  def seenResult(idResult:Int) =
    results_store.find(_.id==idResult) match {
      case Some(value) => value.isSeen= true
      case None =>
    }

  def unseenResult(idResult:Int) = ???

  def getAllResult:List[Result] = results_store.toList

  def getAllResultSeen:List[Result] =
    results_store.filter(_.isSeen).toList

  def getAllResultUnSeen:List[Result] = ???

  def numberOfEventSeen:Int =  ???
}

object ResultService {

  def build:ResultService = new ResultService
}