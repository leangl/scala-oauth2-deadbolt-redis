package matchers

import org.scalatest.matchers.{MatchResult, Matcher}
import play.api.libs.json.{JsPath, JsValue}

trait JsonMatchers {

  class ContainsKeyMatcher(expectedKey: String) extends Matcher[JsValue] {

    def apply(left: JsValue) = {
      MatchResult(
        (left \ expectedKey).toOption.isDefined,
        s"""JSON $left does not contain key "$expectedKey"""",
        s"""JSON $left contains key "$expectedKey""""
      )
    }
  }

  class ContainsPathMatcher(expectedPath: JsPath) extends Matcher[JsValue] {

    def apply(left: JsValue) = {
      MatchResult(
        !(expectedPath(left) isEmpty),
        s"""JSON $left does not contain path "$expectedPath"""",
        s"""JSON $left contains path "$expectedPath""""
      )
    }
  }

  class ContainsValueMatcher(expectedPath: JsPath, expectedValue: JsValue) extends Matcher[JsValue] {

    def apply(left: JsValue) = {

      val matches = expectedPath(left).foldLeft(false) {
        (matches, value) => {
          matches || value.equals(expectedValue)
        }
      }

      MatchResult(
        matches,
        s"""JSON $left does not contain value "$expectedValue" in path "$expectedPath"""",
        s"""JSON $left contains value "$expectedValue" in path "$expectedPath""""
      )
    }
  }

  def containKey(expectedKey: String) = new ContainsKeyMatcher(expectedKey)

  def containPath(expectedPath: JsPath) = new ContainsPathMatcher(expectedPath)

  def containValue(expectedPath: JsPath, expectedValue: JsValue) = new ContainsValueMatcher(expectedPath, expectedValue)
}

object JsonMatchers extends JsonMatchers