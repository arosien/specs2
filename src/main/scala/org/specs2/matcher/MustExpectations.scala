package org.specs2
package matcher

/**
 * This trait provides implicit definitions to transform any value into a MustExpectable
 */
trait MustExpectations extends Expectations {
  implicit def akaMust[T](tm: Expectable[T]) = new MustExpectable(() => tm.value) {
    override private[specs2] val desc = tm.desc
    override def applyMatcher[S >: T](m: =>Matcher[S]): MatchResult[S] = checkFailure(m.apply(this))
  }
  implicit def theValue[T](t: =>T): MustExpectable[T] = createMustExpectable(t)
  implicit def theBlock(t: =>Nothing): MustExpectable[Nothing] = createMustExpectable(t)

  protected def createMustExpectable[T](t: =>T) = new MustExpectable(() => t) {
    override def applyMatcher[S >: T](m: =>Matcher[S]): MatchResult[S] = checkFailure(m.apply(this))
  }
}

/**
 * This trait can be used to remove aka and must methods on any value
 */
trait NoMustExpectations extends MustExpectations {
  override def akaMust[T](tm: Expectable[T]) = super.akaMust(tm)
  override def theValue[T](t: =>T): MustExpectable[T] = super.theValue(t)
  override def theBlock(t: =>Nothing): MustExpectable[Nothing] = super.theBlock(t)
}

object MustExpectations extends MustExpectations

/**
 * This trait provides implicit definitions to transform any value into a MustExpectable, throwing exceptions when
 * a match fails
 */
trait MustThrownExpectations extends ThrownExpectations with MustExpectations {
  override implicit def akaMust[T](tm: Expectable[T]) = new MustExpectable(() => tm.value) {
    override private[specs2] val desc = tm.desc
    override def applyMatcher[S >: T](m: =>Matcher[S]): MatchResult[S] = checkFailure(super.applyMatcher(m))
  }
  override protected def createMustExpectable[T](t: =>T) = new MustExpectable(() => t) {
    override def applyMatcher[S >: T](m: =>Matcher[S]): MatchResult[S] = checkFailure(super.applyMatcher(m))
  }
}
object MustThrownExpectations extends MustThrownExpectations


