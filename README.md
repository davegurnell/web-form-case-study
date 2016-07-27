# Web Form Validator Case Study

Scala case study about validating web forms.
Based on my [talks](https://github.com/davegurnell/functional-data-validation)
at [Scala Exchange London 2014](http://davegurnell.com/articles/functional-data-validation-at-scala-exchange/)
and [Scala Days Amsterdam 2015](http://davegurnell.com/articles/functional-data-validation-at-scala-days/).

Licensed [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Getting Started

To run the example app, use `sbt.sh` or `sbt.bat`:

~~~bash
$ ./sbt.sh run
~~~

To run the unit tests:

~~~bash
$ ./sbt.sh test
~~~

## Synopsis

A validation rule is represented by a trait `Rule[A, B]`:

```scala
sealed trait Rule[A, B] {
  def apply(value: A): Result[B]
}

// various implementations
```

Where `Result` is an data type representing success or failure:

```scala
sealed trait Result[+A]
final case class Pass[A](value: A) extends Result[A]
final case class Fail(messages: List[String]) extends Result[Nothing]
```

There are a few basic types of rules,
including a `PureRule[A, B]` that wraps up a function of type `A => Result[B]`:

```scala
final case class PureRule[A, B](func: A => Result[B]) extends Rule[A, B] {
  def apply(value: A): Result[B] =
    func(value)
}
```

Using this setup we can build rules that act as parsers:

```scala
val parseInt: Rule[String, Int] =
  PureRule { str =>
    try {
      Pass(str.toInt)
    } catch {
      case exn: NumberFormatException =>
        Fail(List(s"Bad integer: " + str))
    }
  }
```

and rules that simply check a property of an input before returning it unaltered:

```scala
def nonEmpty: Rule[String, String] =
  Rule.pure { str =>
    if(str.nonEmpty) {
      Pass(str)
    } else {
      Fail(List("Empty string"))
    }
  }
```

Both `Result` and `Rule` have methods like `map` and `flatMap` that permit various types of combination:

- `result.map`     -- transform the value in a result by passing it through a function;
- `result.flatMap` -- transform the value in a result by passing it through a function that returns another result;
- `result.zip`     -- combine the values inside two independent results to create a tuple;

- `rule.map`       -- transform the results of a rule -- create a rule that runs this rule and transforms the result using a function;
- `rule.andThen`   -- sequence two rules, where rule B is independent of rule A;
- `rule.flatMap`   -- sequence two rules, where rule B depends on rule A;
- `rule.zip`       -- run two rules independently of one another and zip their results.

These handful of methods, when used along-side primitive `PureRule` building blocks,
allow us to represent a wide variety of networks of validation rules.

See `Main.scala` for an example.
