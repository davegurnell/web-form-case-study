package form

import org.scalatest._

class FormDataSpec extends WordSpec with Matchers {
  type FormData = Map[String, String]

  def getField(name: String): Rule[FormData, String] =
    Rule.pure { data =>
      data.get(name)
        .map(Pass.apply)
        .getOrElse(Fail(List(s"Field not found: " + name)))
    }

  def parseInt: Rule[String, Int] =
    Rule.pure { str =>
      try {
        Pass(str.toInt)
      } catch {
        case exn: NumberFormatException =>
          Fail(List(s"Bad integer: " + str))
      }
    }

  def nonEmpty: Rule[String, String] =
    Rule.test[String]("Empty string")(_.nonEmpty)

  case class Person(name: String, age: Int)

  val parsePerson: Rule[FormData, Person] = {
    val nameRule = getField("name") andThen nonEmpty
    val ageRule  = getField("age")  andThen parseInt
    (nameRule zip ageRule) map (Person.tupled)
  }

  "form data example" should {
    "successfully parse good data" in {
      val data   = Map("name" -> "Dave", "age"  -> "37")
      val person = Person("Dave", 37)
      parsePerson(data) should be(Pass(person))
    }

    "report the correct errors for bad data" in {
      val data = Map("name" -> "", "age"  -> "Dave")
      val errors = List("Empty string", "Bad integer: Dave")
      parsePerson(data) should be(Fail(errors))
    }
  }
}