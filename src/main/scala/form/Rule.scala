package form

sealed trait Rule[A, B] {
  def apply(value: A): Result[B] = this match {
    case ZipRule(rule1, rule2)     => rule1(value).zip(rule2(value))
    case PureRule(func)            => func(value)
    case MapRule(rule, func)       => rule(value).map(func)
    case FlatMapRule(rule, func)   => rule(value).flatMap(ans => func(ans).apply(value))
    case AndThenRule(rule1, rule2) => rule1(value).flatMap(rule2.apply)
  }

  def map[C](func: B => C): Rule[A, C] =
    MapRule(this, func)

  def andThen[C](that: Rule[B, C]): Rule[A, C] =
    AndThenRule(this, that)

  def zip[C](that: Rule[A, C]): Rule[A, (B, C)] =
    ZipRule(this, that)
}

final case class PureRule   [X, Y]   (func: X => Result[Y])                    extends Rule[X, Y]
final case class MapRule    [X, Y, Z](rule: Rule[X, Y], func: Y => Z)          extends Rule[X, Z]
final case class FlatMapRule[X, Y, Z](rule: Rule[X, Y], func: Y => Rule[X, Z]) extends Rule[X, Z]
final case class AndThenRule[X, Y, Z](rule1: Rule[X, Y], rule2: Rule[Y, Z])    extends Rule[X, Z]
final case class ZipRule    [X, Y, Z](rule1: Rule[X, Y], rule2: Rule[X, Z])    extends Rule[X, (Y, Z)]

object Rule {
  def pure[A, B](func: A => Result[B]): Rule[A, B] =
    PureRule(func)

  def test[A](msg: String)(func: A => Boolean): Rule[A, A] =
    PureRule(value => if(func(value)) Pass(value) else Fail(List(msg)))
}
