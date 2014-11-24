package org.leialearns.logic.utilities

import java.io.{UnsupportedEncodingException, IOException, Writer}
import java.math.BigInteger

import org.leialearns.utilities.ExceptionWrapper
import org.slf4j.LoggerFactory

trait PrefixEncoderTrait { self: Writer =>
  val logger = LoggerFactory.getLogger(getClass)

  def append(i: Int): Unit = {
    append(BigInt.int2bigInt(i))
  }

  def append(i: Long): Unit = {
    append(BigInt.long2bigInt(i))
  }

  def append(i: BigInteger): Unit = {
    append(BigInt.javaBigInteger2bigInt(i))
  }

  def append(i: BigInt): Unit = {
    try {
      self.write(PrefixFreeBigInt.prefixEncode(i))
    } catch {
      case exception: IOException =>
        throw ExceptionWrapper.wrap(exception)
    }
    appendComment("\n")
  }

  def append(i: Int, length: Int): Unit = {
    append(BigInt.int2bigInt(i), length)
  }

  def append(i: Long, length: Int): Unit = {
    append(BigInt.long2bigInt(i), length)
  }

  def append(i: BigInteger, length: Int): Unit = {
    append(BigInt.javaBigInteger2bigInt(i), length)
  }

  def append(i: BigInt, length: Int): Unit = {
    if (i < 0) {
      throw new IllegalArgumentException("Cannot prefix-encode a negative number: " + i)
    }
    val denotation: String = i.toString(2).replace('0', 'O').replace('1', 'I')
    if (logger.isTraceEnabled) {
      logger.trace(s"Denotation: [$denotation]")
    }

    val padding = length - denotation.length
    if (padding < 0) {
      throw new IllegalArgumentException(s"Number too large: $length: ${i.toString(16).toUpperCase}")
    }
    try {
      self.write("O" * padding)
      self.write(denotation)
    } catch {
      case exception: IOException =>
        throw ExceptionWrapper.wrap(exception)
    }

    appendOriginal(String.valueOf(i))
    appendComment("\n")
  }

  def append(s: String): Unit = {
    var bytes: Array[Byte] = null
    try {
      bytes = s.getBytes("UTF-8")
    }
    catch {
      case exception: UnsupportedEncodingException =>
        throw ExceptionWrapper.wrap(exception)
    }
    append(bytes.length)
    for (b <- bytes) {
      append(b & 0xFF, 8)
    }
    val quote = "\""
    appendOriginal(s"$quote${s.replace('"', '\'')}$quote")
    appendComment("\n")
  }

  def append(b: java.lang.Boolean): Unit = {
    append(Boolean2boolean(b))
  }

  def append(b: Boolean): Unit = {
    append(if (b) 1 else 0, 1)
    appendOriginal(b.toString)
    appendComment("\n")
  }

  def append(constant: Enum[_]): Unit = {
    val last: Int = constant.getClass.getEnumConstants.length - 1
    if (last > 0) {
      val bitLength: Int = BigInteger.valueOf(last).bitLength
      append(constant.ordinal, bitLength)
      appendOriginal(constant.name)
      appendComment("\n")
    }
  }

  def appendOriginal(original: String): Unit = {
    appendComment("(")
    appendComment(original)
    appendComment(")")
  }

  def appendComment(comment: String): Unit = {
    try {
      self.write(comment.replace('O', '0').replace('I', '1'))
    } catch {
      case exception: IOException =>
        throw ExceptionWrapper.wrap(exception)
    }
  }
}
