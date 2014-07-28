package org.leialearns.logic.utilities

import org.scalatest.FunSuite
import org.slf4j.LoggerFactory
import java.io.{IOException, StringReader}

class TestPrefixFree extends FunSuite {
  val logger = LoggerFactory.getLogger(getClass)

  def singlePrefixEncodeBigInteger(n: BigInt) {
    val encoded = PrefixFree.prefixEncode(n)
    logger.info(s"Prefix-free: $n: [$encoded]")
    val encodedReader = new StringReader(encoded)
    val decoded = PrefixFree.prefixDecode(encodedReader)
    intercept[IOException] {
      PrefixFree.readBit(encodedReader)
    }
    assert(n == decoded)
  }

  test("Composition of prefixDecode and prefixEncode should be the identity function") {
    for (i <- 0 to 40) {
      singlePrefixEncodeBigInteger(i)
    }
    for (i <- 1 to 16) {
      val n = BigInt.int2bigInt(2).pow(i) - 1
      val m = n - 1
      singlePrefixEncodeBigInteger(m)
      singlePrefixEncodeBigInteger(n)
    }
  }
}
