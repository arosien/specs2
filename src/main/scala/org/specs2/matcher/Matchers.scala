package org.specs2
package matcher

import execute._

/**
 * Trait aggregating all of specs2 matchers 
 */
trait Matchers extends AnyMatchers 
                  with IterableMatchers 
                  with MapMatchers
                  with StringMatchers 
                  with XmlMatchers
                  with BeHaveMatchers 
                  with ExceptionMatchers
                  with NumericMatchers
                  with OptionMatchers
                  with EitherMatchers
                  with EventuallyMatchers
                  with FileMatchers
                  with ResultMatchers
                  with MatchersImplicits

object Matchers extends Matchers

trait MustMatchers extends Matchers with MustExpectations
trait ShouldMatchers extends Matchers with ShouldExpectations
trait JUnitMustMatchers extends Matchers with JUnitMustExpectations
trait JUnitShouldMatchers extends Matchers with JUnitShouldExpectations

trait MustThrownMatchers extends Matchers with MustThrownExpectations
trait ShouldThrownMatchers extends Matchers with ShouldThrownExpectations
