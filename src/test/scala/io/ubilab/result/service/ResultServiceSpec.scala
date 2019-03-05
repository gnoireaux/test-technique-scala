package io.ubilab.result.service

import io.ubilab.result.model.Result
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class ResultServiceSpec extends FunSpec with Matchers {

  describe("Step 1 : initialisation du projet avec 0 et 1 résultat") {

    val resultService = ResultService.build

    it("devrait être initialisé avec une liste de résultat vide") {

      resultService.getAllResult shouldEqual List()
    }
  }

  describe("Après l'ajout d'un résultat,") {

    val resultService = ResultService.build

    resultService.addResult(
      Result(
        46,
        76,
        List(42),
        false,
        Nil,
        "test"
      )
    )

    it("devrait avoir une liste de 1 résultat non vu") {

      resultService.getAllResult.length shouldEqual 1

    }

    it("devrait avoir une liste de 1 résultat vu après la vision de ce résultat") {
      resultService.seenResult(46)
      resultService.getAllResultSeen.length shouldEqual 1
      resultService.getAllResult.head.isSeen shouldEqual true
    }

  }

  describe("Après l'ajout de 3 résultats,") {

    // init le service avec 3 résultats
    val resultService = ResultService.build
    val result_1 = Result(46, 76, List(42), false, Nil, "test")
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
      resultService.seenResult(result_1.id)
      resultService.getAllResultSeen.length shouldEqual 1
    }

    it("devrait avoir les 3 résultats vus dans la liste après qu'ils soient tous vus.") {
      resultService.seenResult(result_1.id)
      resultService.seenResult(result_2.id)
      resultService.seenResult(result_3.id)
      resultService.getAllResultSeen.length shouldEqual 3
    }

    it("devrait avoir plus que 2 résultats vue dans la liste après qu'il soit tous vue puis 1 ou la vue est enlevé") {
      pending
      true shouldEqual false
    }

    it("ne devrait pas planter après la vision d\\'un résultat non ajouté") {
      pending
      true shouldEqual false
    }

  }


  describe("Après l'ajout de 3 résultats,") {
    pending
    // init le service avec 3 résultats
    it("devrait avoir la list des résultat dans l'order de création ( en se basant sur les events de création)") {
      true shouldEqual false
    }

    it("devrait avoir 1 event a la date de maintenant quand 1 résultat est vue") {
      true shouldEqual false
    }

    it("devrait avoir 2 events avec 2 dates différent après la vision d'un résultat puis la suppression de la vision") {
      true shouldEqual false
    }

    it("devrait avoir une fonction qui retourne une liste ordonnée des résultats par rapport au dernier modifier") {
      true shouldEqual false
    }
  }


  describe("N'hésitez pas a proposer de nouveaux tests") {}
}
