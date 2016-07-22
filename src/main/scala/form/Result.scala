package form

sealed trait Result[+A] {
  def map[B](func: A => B): Result[B] =
    this match {
      case Pass(value) => Pass(func(value))
      case Fail(msgs)  => Fail(msgs)
    }

  def flatMap[B](func: A => Result[B]): Result[B] =
    this match {
      case Pass(value) => func(value)
      case Fail(msgs)  => Fail(msgs)
    }

  def zip[B](that: Result[B]): Result[(A, B)] =
    (this, that) match {
      case (Pass(a), Pass(b)) => Pass((a, b))
      case (Pass(a), Fail(b)) => Fail(b)
      case (Fail(a), Pass(b)) => Fail(a)
      case (Fail(a), Fail(b)) => Fail(a ++ b)
    }
}

final case class Pass[A](value: A) extends Result[A]

final case class Fail(messages: List[String]) extends Result[Nothing]
