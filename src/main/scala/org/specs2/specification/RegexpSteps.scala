package org.specs2
package specification

import execute._
import specification.StandardFragments.{Br, End}

/**
 * This trait provides building block to create steps and examples from regular expression.
 *
 * It is used to implement a Given-When-Then way of describing systems.
 */
trait RegexpSteps {

  implicit def RegexpFragmentToFragments(r: RegexpFragment): Fragments = r.fs

  abstract class RegexpStep[P, T](regexp: String) {
    def extractString(text: String) = {
      val RegExp = regexp.r
      val RegExp(extracted) = text
      extracted
    }
  }

  private def tryOrError[T](t: =>T): Either[Result, T] = try(Right(t)) catch { case (e:Exception) => Left(Error(e)) }

  abstract class Given[T](regexp: String) extends RegexpStep[Unit, T](regexp) {
    def extractContext(text: String): Either[Result, T] = tryOrError(extract(text))
    def extract(text: String): T
  }
  abstract class When[P, T](regexp: String) extends RegexpStep[P, T](regexp) {
    def extractContext(p: Either[Result, P], text: String): Either[Result, T] = p match {
      case Left(l)  => Left(Skipped(l.message))
      case Right(r) => tryOrError(extract(r, text))
    }
    def extract(p: P, text: String): T
  }

  abstract class Then[T](regexp: String) extends RegexpStep[Either[Result, T], (T, Result)](regexp) {
    def extractContext(t: Either[Result, T], text: String): Either[Result, (T, Result)] = t match {
      case Left(l)  => Left(Skipped(l.message))
      case Right(r) => tryOrError((r, extract(r, text)))
    }
    def extract(t: T, text: String): Result
  }

  trait RegexpFragment {
    type RegexpType <: RegexpFragment
    val fs: Fragments
    def add(f: Fragment): RegexpType
    def ^(f: Text): RegexpType = add(f)
    def ^(f: Br): RegexpType = add(f)
    def ^(f: End) = fs.add(f)
    def ^(fs2: Fragments) = fs.add(fs2)
  }

  case class PreStep[T](context: () => Either[Result, T], fs: Fragments) extends RegexpFragment {
    type RegexpType = PreStep[T]
    def ^(toExtract: String) = new PreStepText(toExtract, context, fs)
    def add(f: Fragment): RegexpType = new PreStep(context, fs.add(f))
  }

  case class PreStepText[T](text: String, context: () => Either[Result, T], fs: Fragments) extends RegexpFragment {
    type RegexpType = PreStepText[T]
    def ^[R](step: When[T, R]) = {
      lazy val extracted = step.extractContext(context(), text)
      new PreStep(() => extracted, fs.add(Text(text)).add(Step.fromEither(extracted)))
    }
    def ^(step: Then[T]) = {
     lazy val extracted = step.extractContext(context(), text)
     new PostStep(() => toContext(extracted), fs.add(Example(text, toResult(extracted))))
    }
    def add(f: Fragment): RegexpType = new PreStepText(text, context, fs.add(f))
  }

  case class PostStep[T](context: () => Either[Result, T], fs: Fragments) extends RegexpFragment {
    type RegexpType = PostStep[T]
    def ^(toExtract: String) = new PostStepText(toExtract, context, fs)
    def add(f: Fragment): RegexpType = new PostStep(context, fs.add(f))
  }

  case class PostStepText[T](text: String, context: () => Either[Result, T], fs: Fragments) extends RegexpFragment {
    type RegexpType = PostStepText[T]
    def ^(step: Then[T]) = {
      lazy val extracted = step.extractContext(context(), text)
      new PostStep(() => extracted, fs.add(Example(text, toResult(extracted))))
    }
    def add(f: Fragment): RegexpType = new PostStepText(text, context, fs.add(f))
  }

  private def toResult[T](context: =>Either[Result, (T, Result)]) = {
    context match {
      case Left(l)  => l
      case Right((t, r)) => r
    }
  }
  private def toContext[T](context: =>Either[Result, (T, Result)]): Either[Result, T] = {
    context match {
      case Left(l)  => Left(l)
      case Right((t, r)) => Right(t)
    }
  }
}