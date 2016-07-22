package form

import org.scalatest._

class RuleSpec extends WordSpec with Matchers {
  "test" should {
    val rule = Rule.test[Int]("Too low")(num => num >= 0)

    "create a rule that passes and fails" in {

      rule( 1) should be(Pass(1))
      rule( 0) should be(Pass(0))
      rule(-1) should be(Fail(List("Too low")))
    }

    "not transform a fail" in {
      Fail(List("A", "B")) map (_.toString) should be(Fail(List("A", "B")))
    }
  }

  "map" should {
    val rule1 = Rule.test[Int]("Too low")(num => num >= 0)
    val rule2 = rule1 map (_ * 2)

    "transform the result of a rule" in {
      rule2(+1) should be(Pass(2))
      rule2( 0) should be(Pass(0))
      rule2(-1) should be(Fail(List("Too low")))
    }
  }

  "andThen" should {
    val rule1 = Rule.test[Int]("Too low")(num => num >= 0)
    val rule2 = Rule.test[Int]("Too high")(num => num < 10)
    val rule3 = rule1 andThen rule2

    "fail if either operand fails" in {
      rule3( 0) should be(Pass(0))
      rule3( 9) should be(Pass(9))
      rule3(-1) should be(Fail(List("Too low")))
      rule3(10) should be(Fail(List("Too high")))
    }
  }

  "zip" should {
    val rule1 = Rule.test[Int]("Too low")(num => num >= 0)
    val rule2 = Rule.test[Int]("Too high")(num => num < 10)
    val rule3 = rule1 zip rule2

    "fail if either operand fails" in {
      rule3( 0) should be(Pass((0, 0)))
      rule3( 9) should be(Pass((9, 9)))
      rule3(-1) should be(Fail(List("Too low")))
      rule3(10) should be(Fail(List("Too high")))
    }
  }
}
