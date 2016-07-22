package form

sealed trait Rule[A, B] {
  def apply(value: A): Result[B]

  def map[C](func: B => C): Rule[A, C] =
    MapRule(this, func)

  def flatMap[C](func: B => Rule[A, C]): Rule[A, C] =
    FlatMapRule(this, func)

  def andThen[C](that: Rule[B, C]): Rule[A, C] =
    AndThenRule(this, that)

  def zip[C](that: Rule[A, C]): Rule[A, (B, C)] =
    ZipRule(this, that)
}

final case class PureRule[A, B](func: A => Result[B]) extends Rule[A, B] {
  def apply(value: A): Result[B] =
    func(value)
}

final case class MapRule[A, B, C](rule: Rule[A, B], func: B => C)  extends Rule[A, C] {
  def apply(value: A): Result[C] =
    rule(value) map (func)
}

final case class FlatMapRule[A, B, C](rule: Rule[A, B], func: B => Rule[A, C]) extends Rule[A, C] {
  def apply(value: A): Result[C] =
    rule(value) flatMap (ans => func(ans)(value))
}

final case class AndThenRule[A, B, C](rule1: Rule[A, B], rule2: Rule[B, C]) extends Rule[A, C] {
  def apply(value: A): Result[C] =
    rule1(value) flatMap (rule2.apply)
}

final case class ZipRule[A, B, C](rule1: Rule[A, B], rule2: Rule[A, C]) extends Rule[A, (B, C)] {
  def apply(value: A): Result[(B, C)] =
    rule1(value) zip rule2(value)
}


object Rule {
  def pure[A, B](func: A => Result[B]): Rule[A, B] =
    PureRule(func)

  def test[A](msg: String)(func: A => Boolean): Rule[A, A] =
    PureRule(value => if(func(value)) Pass(value) else Fail(List(msg)))
}
