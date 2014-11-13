package org.leialearns.logic.utilities

import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.io.{IOException, Reader}

object PrefixFree {
  val logger = LoggerFactory.getLogger(getClass)
  val one = BigInt.int2bigInt(1)

  /**
    * Returns an ascii form of the binary representation of the given big integer. This form uses the capitals
    * 'O' and 'I' to distinguish them from the numeric digits that are used in composite representations of
    * prefix-free codes.
    *
    * @param n The number to represent
    * @return The string of binary digits
    */
  def toBinary(n: BigInt): String = {
    n toString 2 replace ('0', 'O') replace ('1', 'I')
  }

  /** @see #toBinary(BigInt) */
  def toBinary(n: BigInteger): String = {
    toBinary(new BigInt(n))
  }

  /**
    * Returns an ascii form of the prefix-free encoding of the given number. No prefix-free encoding is a prefix of
    * the prefix-free encoding of another number. The actual bit sequence consisting of the capitals 'O' and 'I' is
    * enriched with digits and symbols to enhance the readability of the code.
    *
    * @param n The number to represent
    * @return The prefix-free code for the given number
    */
  def prefixEncode(n: BigInt): String = {
    logger.debug("Start prefix encode big integer: [%s]" format n)
    if (n < 0) {
      throw new IllegalArgumentException("Value should be non-negative")
    }
    prefixEncodeChunks(n + 1, 'I')
  }

  /** @see #prefixEncode(BigInt) */
  def prefixEncode(n: BigInteger): String = {
    prefixEncode(new BigInt(n))
  }

  private def prefixEncodeChunks(n: BigInt, lastChunkFlag: Char): String = {
    logger.trace("Prefix encode big integer: [" + n + "]")
    if (n == one) {
      lastChunkFlag + ":1(1)"
    } else {
      val remainder = toBinary(n).substring(1)
      val builder: StringBuilder = new StringBuilder()
      builder.append(prefixEncodeChunks(remainder.length, 'O'))
      builder.append('/')
      builder.append(lastChunkFlag)
      builder.append(":1")
      builder.append(remainder)
      builder.append('(')
      builder.append(n.toString())
      builder.append(')')
      builder.toString()
    }
  }

  def prefixDecode(reader: Reader): BigInt = {
    var value: BigInt = 0
    var isLength = false
    var length = 0L
    do {
      isLength = readBit(reader) == ZERO
      length = value.longValue()
      value = 1
      for (i <- 0L to length - 1L) {
        value = value << 1
        if (readBit(reader) == ONE) {
          value += 1
        }
      }
    } while (isLength)
    value - 1
  }

  def readBit(reader: Reader): Bit = {
    val n = reader.read()
    if (n == -1) {
      throw new IOException("End of reader")
    }
    n.asInstanceOf[Char] match {
      case 'O' => ZERO
      case 'I' => ONE
      case _ => readBit(reader)
    }
  }

  def asInt(bit: Bit): Int = {
    bit match {
      case ZERO => 0
      case ONE => 1
    }
  }
}
