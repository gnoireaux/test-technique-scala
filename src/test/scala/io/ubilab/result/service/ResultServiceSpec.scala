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
    val viewerId = ViewerId(42)
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
      resultService.seenResult(ResultId(result), viewerId)
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
    val viewerId = ViewerId(42)
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
      resultService.seenResult(result_1, viewerId)
      resultService.getAllResultSeen.length shouldEqual 1
    }

    it("devrait avoir les 3 résultats vus dans la liste après qu'ils soient tous vus.") {
      resultService.seenResult(result_1, viewerId)
      resultService.seenResult(result_2, viewerId)
      resultService.seenResult(result_3, viewerId)
      resultService.getAllResultSeen.length shouldEqual 3
    }

    it("devrait n'avoir plus que 2 résultats vus dans la liste après qu'ils soient tous vus puis 1 ou la vue est enlevée") {
      resultService.getAllResultSeen.length shouldEqual 3
      resultService.unseenResult(ResultId(result_1), viewerId)
      resultService.getAllResultSeen.length shouldEqual 2

    }

    it("ne devrait pas planter après la vision d'un résultat non ajouté") {
      val notAddedResult = result_1.copy(id = result_1.id + 42)
      noException should be thrownBy resultService.seenResult(notAddedResult, viewerId)
      // assuming it is true also for unseeing a result
      noException should be thrownBy resultService.unseenResult(ResultId(notAddedResult), viewerId)
    }

  }


  describe("Step 4 : Après l'ajout de 3 résultats (events)") {
    val resultService = ResultService.build
    val result_1 = Result(46, 76, List(42,43), "test")
    Thread.sleep(1) // need the time of creation in milliseconds to be different for comparison
    val result_2 = result_1.copy(id = result_1.id + 1)
    Thread.sleep(1)
    val result_3 = result_1.copy(id = result_1.id + 2)
    val viewerId = ViewerId(42)
    val another_viewerId = ViewerId(43)
    resultService.addResult(result_3)
    resultService.addResult(result_2)
    resultService.addResult(result_1)

    it("devrait avoir la liste des résultats dans l'ordre de création (en se basant sur les events de création)") {
      resultService.getAllResult shouldEqual List(result_1, result_2, result_3)
    }

    it("devrait avoir 1 event a la date de maintenant quand 1 résultat est vu, avec le viewer id") {
      resultService.seenResult(result_1, viewerId)
      resultService.getAllResultSeen.length shouldEqual 1
      resultService.getAllResultSeen.head.isSeen shouldEqual true
      val event: SeenStateEvent = resultService.getAllResultSeen.head.seenStateEvents.last
      event shouldBe a[Seen]
      event.createdAt.getTime shouldBe (new java.util.Date()).getTime +- 1000
      event.idOwner shouldEqual viewerId.id
    }

    it("devrait avoir 2 events avec 2 dates différentes après la vision d'un résultat puis la suppression de la vision") {
      resultService.unseenResult(ResultId(result_1), viewerId)
      resultService.getAllResultSeen.length shouldEqual 0
      val seen_event = result_1.seenStateEvents.head
      val unseen_event = result_1.seenStateEvents.last
      seen_event shouldBe a[Seen]
      unseen_event shouldBe a[Unseen]
      unseen_event.idOwner shouldEqual viewerId.id
      seen_event.createdAt shouldNot be theSameInstanceAs unseen_event.createdAt
    }

    it("devrait avoir une fonction qui retourne une liste ordonnée des résultats par rapport au derniers modifiés") {
      // without more information about how the data will be consumed,
      // I chose to order from older to newer modification.
      resultService.getAllResultsLastModified shouldEqual List(result_2, result_3, result_1)
    }

    it("should verify that the (un)viewer is a recipient") {
      //  if not recipient: do not add the event and then fail
      val viewerId_not_recipient = ViewerId(999)

      result_1.seenStateEvents.length shouldEqual 2

      val response_to_seen = resultService.seenResult(result_1, viewerId_not_recipient)
      response_to_seen shouldBe a[Failure[_]]
      response_to_seen.asInstanceOf[Failure[_]].exception.getMessage should
        include (viewerId_not_recipient.id.toString)

      result_1.seenStateEvents.length shouldEqual 2

      val response_to_unseen = resultService.unseenResult(ResultId(result_1), viewerId_not_recipient)
      response_to_unseen shouldBe a[Failure[_]]
      response_to_unseen.asInstanceOf[Failure[_]].exception.getMessage should
        include (viewerId_not_recipient.id.toString)

      result_1.seenStateEvents.length shouldEqual 2
    }

    it("should count the number of Seen events") {
      resultService.numberOfEventSeen shouldEqual 1
      resultService.seenResult(result_1, viewerId)
      resultService.numberOfEventSeen shouldEqual 2
      resultService.seenResult(result_2, viewerId)
      resultService.numberOfEventSeen shouldEqual 3
    }
    it("should count the people having seen a result (user having unseen result does not count)") {
      resultService.numberOfPeopleSeen(ResultId(result_3)) shouldEqual 0
      resultService.seenResult(ResultId(result_3), viewerId)
      resultService.numberOfPeopleSeen(ResultId(result_3)) shouldEqual 1
      resultService.unseenResult(ResultId(result_3), viewerId)
      resultService.numberOfPeopleSeen(ResultId(result_3)) shouldEqual 0
      resultService.seenResult(ResultId(result_3), another_viewerId)
      resultService.numberOfPeopleSeen(ResultId(result_3)) shouldEqual 1
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
    it("should be able to tell if a list of SeenStateEvents ends in a Seen event") {
      val no_event         = List[SeenStateEvent]()
      val one_seen         = List[SeenStateEvent](Seen(0))
      val two_seen         = List[SeenStateEvent](Seen(0), Seen(0)) // might happen and would defeat testing for an odd length for the list
      val seen_then_unseen = List[SeenStateEvent](Seen(0), Unseen(0))
      Result.endsInASeen(no_event) shouldEqual false
      Result.endsInASeen(one_seen) shouldEqual true
      Result.endsInASeen(two_seen) shouldEqual true
      Result.endsInASeen(seen_then_unseen) shouldEqual false
    }
  }

  describe("N'hésitez pas a proposer de nouveaux tests") {}
}
