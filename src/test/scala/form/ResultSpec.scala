package form

import org.scalatest._

class ResultSpec extends WordSpec with Matchers {
  "map" should {
    "transform a pass" in {
      Pass(1) map (_.toString) should be(Pass("1"))
    }

    "not transform a fail" in {
      Fail(List("A", "B")) map (_.toString) should be(Fail(List("A", "B")))
    }
  }

  "flatMap" should {
    "transform a pass to a pass" in {
      Pass(1) flatMap (n => Pass(n + 1)) should be(Pass(2))
    }

    "transform a pass to a fail" in {
      Pass(1) flatMap (n => Fail(List("A", "B"))) should be(Fail(List("A", "B")))
    }

    "not transform a fail" in {
      Fail(List("A", "B")) flatMap (n => Pass(n.toString)) should be(Fail(List("A", "B")))
    }
  }

  "zip" should {
    "combine a pass and a pass" in {
      Pass(1) zip Pass(2) should be(Pass((1, 2)))
    }

    "combine a pass and a fail" in {
      Pass(1) zip Fail(List("A", "B")) should be(Fail(List("A", "B")))
    }

    "combine a fail and a pass" in {
      Fail(List("A", "B")) zip Pass(1) should be(Fail(List("A", "B")))
    }

    "combine a fail and a fail" in {
      Fail(List("A", "B")) zip Fail(List("C", "D")) should be(Fail(List("A", "B", "C", "D")))
    }
  }
}
