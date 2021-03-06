package org.specs2
package specification

import ExecutedFragment._
import execute.Error


/**
 * A specification with a name and all of its fragments already executed
 */
case class ExecutedSpecification(name: SpecName, fs: Seq[ExecutedFragment]) {

  def includedLinkedSpecifications: Seq[ExecutedSpecStart]  = fragments collect isIncludeLink
  def includedSeeOnlySpecifications: Seq[ExecutedSpecStart] = fragments collect isSeeOnlyLink

  /** @return the executed fragments */
  def fragments = fs

  /** @return true if there are errors */
  def hasErrors = fs.exists { case r: ExecutedResult if r.isError => true; case _ => false }

  /** @return true if there are issues  */
  def hasIssues = fs.exists { case r: ExecutedResult if r.isIssue => true; case _ => false }
}

