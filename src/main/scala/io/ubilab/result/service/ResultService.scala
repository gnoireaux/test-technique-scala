package io.ubilab.result.service

import io.ubilab.result.model.Result

import scala.collection.mutable.ListBuffer
import scala.util.{Try,Success,Failure}

class ResultService {
  private val results_store = ListBuffer[Result]()

  def addResult(result:Result): Try[ListBuffer[Result]] = Try {
    results_store.exists(_.id == result.id) match {
      case true => throw new IllegalArgumentException("A result already exists with the same id.")
      case false => results_store += result
    }
  }


  def seenResult(idResult:Int) =
    results_store.find(_.id==idResult) match {
      case Some(value) => value.isSeen= true
      case None =>
    }

  def unseenResult(idResult:Int) = ???

  def getAllResult:List[Result] = results_store.toList

  def getAllResultSeen:List[Result] =
    results_store.filter(_.isSeen).toList

  def getAllResultUnSeen:List[Result] =
    results_store.filter(!_.isSeen).toList

  def numberOfEventSeen:Int =  ???
}

object ResultService {

  def build:ResultService = new ResultService
}