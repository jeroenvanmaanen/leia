package org.leialearns.logic.utilities

import scala.util.control.Breaks
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory
import java.io.{IOException, StringReader}

class TestPrefixFree extends FunSuite {
  val logger = LoggerFactory.getLogger(getClass)

  def singlePrefixEncodeBigInteger(n: BigInt) {
    val encoded = PrefixFreeBigInt.prefixEncode(n)
    logger.info(s"Prefix-free: $n: [$encoded]")
    val encodedReader = new StringReader(encoded)
    val decoded = PrefixFreeBigInt.prefixDecode(encodedReader)
    intercept[IOException] {
      PrefixFreeBigInt.readBit(encodedReader)
    }
    assert(n == decoded)

    val bitReader = new StringReader(encoded)
    val loop = new Breaks
    var i = 0
    loop.breakable {
      while (true) {
        try {
          PrefixFreeBigInt.readBit(bitReader)
          i += 1
        } catch {
          case exception: IOException =>
            loop.break()
        }
      }
    }
    val length = PrefixFreeBigInt.descriptionLength(n)
    assert(i == length)
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
