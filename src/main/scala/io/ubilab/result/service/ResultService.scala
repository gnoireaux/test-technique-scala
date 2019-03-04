package io.ubilab.result.service

import io.ubilab.result.model.Result

import scala.collection.mutable.ListBuffer

class ResultService {
  val results_store = ListBuffer[Result]()

  def addResult(result:Result) = results_store += result


  def seenResult(idResult:Int) = ???

  def unseenResult(idResult:Int) = ???

  def getAllResult():List[Result] = results_store.toList

  def getAllResultSeen():List[Result] = ???
  def getAllResultUnSeen():List[Result] = ???

  def numberOfEventSeen:Int =  ???
}

object ResultService {

  def build:ResultService = new ResultService
}