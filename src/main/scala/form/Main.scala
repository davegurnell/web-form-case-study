package form

object Main extends App {
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

  val validateName   = getField("name") andThen nonEmpty
  val validateAge    = getField("age")  andThen parseInt
  val validatePerson = (validateName zip validateAge) map (Person.tupled)

  val goodData: FormData =
    Map(
      "name" -> "Dave",
      "age"  -> "37"
    )

  val badData: FormData =
    Map(
      "name" -> "",
      "age"  -> "Dave"
    )

  println(validatePerson(goodData))
  println(validatePerson(badData))
}
