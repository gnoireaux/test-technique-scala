package io.ubilab.result.service

import io.ubilab.result.model._
import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success, Try}

class ResultServiceSpec extends FunSpec with Matchers {

  describe("Step 1 : initialisation du projet avec 0 et 1 résultat") {

    val resultService = ResultService.build

    it("devrait être initialisé avec une liste de résultat vide") {

      resultService.getAllResult shouldEqual List()
    }
  }

  describe("Step 2 : Après l'ajout d'un résultat,") {

    val resultService = ResultService.build
    val result = Result(
      46,
      76,
      List(42),
      "test"
    )

    resultService.addResult(result)

    it("devrait avoir une liste de 1 résultat non vu") {

      resultService.getAllResult.length shouldEqual 1

    }

    it("devrait avoir une liste de 1 résultat vu après la vision de ce résultat") {
      // keep testing the method taking an Id
      // because stable APIs are nice
      resultService.seenResult(ResultId(result.id), ViewerId(42))
      resultService.getAllResultSeen.length shouldEqual 1
      resultService.getAllResult.head.isSeen shouldEqual true
    }

  }

  describe("Step 3 : Après l'ajout de 3 résultats,") {

    // init le service avec 3 résultats
    val resultService = ResultService.build
    val result_1 = Result(46, 76, List(42), "test")
    val result_2 = result_1.copy(id = result_1.id + 1)
    val result_3 = result_1.copy(id = result_1.id + 2)
    resultService.addResult(result_1)
    resultService.addResult(result_2)
    resultService.addResult(result_3)

    it("devrait avoir une liste de 3 résultats non vus après l'ajout de 3 résultats.") {
      resultService.getAllResultUnSeen.length shouldEqual 3
    }

    it("ne devrait pas autoriser l'ajout d'un résultat avec un id existant") {
      resultService.addResult(result_1.copy(id = result_1.id)) shouldBe a[Failure[_]] // sameness of id is explicit!
    }

    it("devrait avoir 1 résultat vu dans la liste après la vision d'un résultat") {
      resultService.seenResult(result_1, ViewerId(42))
      resultService.getAllResultSeen.length shouldEqual 1
    }

    it("devrait avoir les 3 résultats vus dans la liste après qu'ils soient tous vus.") {
      resultService.seenResult(result_1, ViewerId(42))
      resultService.seenResult(result_2, ViewerId(42))
      resultService.seenResult(result_3, ViewerId(42))
      resultService.getAllResultSeen.length shouldEqual 3
    }

    it("devrait n'avoir plus que 2 résultats vus dans la liste après qu'ils soient tous vus puis 1 ou la vue est enlevée") {
      resultService.getAllResultSeen.length shouldEqual 3
      resultService.unseenResult(result_1.id)
      resultService.getAllResultSeen.length shouldEqual 2

    }

    it("ne devrait pas planter après la vision d\\'un résultat non ajouté") {
      val notAddedResult = result_1.copy(id = result_1.id + 42)
      noException should be thrownBy resultService.seenResult(notAddedResult, ViewerId(42))
    }

  }


  describe("Step 4 : Après l'ajout de 3 résultats (events)") {
    val resultService = ResultService.build
    val result_1 = Result(46, 76, List(42), "test")
    Thread.sleep(1) // need the time of creation in milliseconds to be different for comparison
    val result_2 = result_1.copy(id = result_1.id + 1)
    Thread.sleep(1)
    val result_3 = result_1.copy(id = result_1.id + 2)
    resultService.addResult(result_3)
    resultService.addResult(result_2)
    resultService.addResult(result_1)

    it("devrait avoir la liste des résultats dans l'ordre de création (en se basant sur les events de création)") {
      resultService.getAllResult shouldEqual List(result_1, result_2, result_3)
    }

    it("devrait avoir 1 event a la date de maintenant quand 1 résultat est vu, avec le viewer id") {
      val viewerId = ViewerId(42)
      resultService.seenResult(result_1, viewerId)
      resultService.getAllResultSeen.length shouldEqual 1
      resultService.getAllResultSeen.head.isSeen shouldEqual true
      val event: SeenStateEvent = resultService.getAllResultSeen.head.seenStateEvents.last
      event shouldBe a[Seen]
      event.createdAt.getTime shouldBe (new java.util.Date()).getTime +- 1000
      event.idOwner shouldEqual viewerId.id
    }

    it("devrait avoir 2 events avec 2 dates différentes après la vision d'un résultat puis la suppression de la vision") {
      resultService.unseenResult(result_1.id)
      resultService.getAllResultSeen.length shouldEqual 0
      val seen_event = result_1.seenStateEvents.head
      val unseen_event = result_1.seenStateEvents.last
      seen_event shouldBe a[Seen]
      unseen_event shouldBe a[Unseen]
      seen_event.createdAt shouldNot be theSameInstanceAs unseen_event.createdAt
    }

    it("devrait avoir une fonction qui retourne une liste ordonnée des résultats par rapport au derniers modifiés") {
      // without more information about how the data will be consumed,
      // I chose to order from older to newer modification.
      resultService.getAllResultsLastModified shouldEqual List(result_2, result_3, result_1)
    }

    it("should count the number of Seen events") {
      resultService.numberOfEventSeen shouldEqual 1
      resultService.seenResult(result_1, ViewerId(42))
      resultService.numberOfEventSeen shouldEqual 2
      resultService.seenResult(result_2, ViewerId(42))
      resultService.numberOfEventSeen shouldEqual 3
    }
  }

  describe( "Result") {
    it("should from the start have a creation event") {
      val result = Result(46, 76, List(42), "test")
      result.created shouldBe a[Created]
      result.events.length shouldEqual 1
      result.events.head shouldBe a[Created]
    }
    it("assuming creator == owner") {
      val result = Result(46, 76, List(42), "test")
      result.created.idOwner shouldEqual result.idOwner
      // data is in two places but they are built in agreement.
      // and the two places being immutable they should remain in agreement.
    }
  }

  describe("N'hésitez pas a proposer de nouveaux tests") {}
}
