package org.leialearns.logic.utilities

import java.io.{UnsupportedEncodingException, Reader}
import java.math.BigInteger

import org.leialearns.common.ExceptionWrapper

import scala.collection.immutable.HashMap
import scala.math.BigInt

trait PrefixDecoderTrait { self: Reader =>
  private final var helpers: Map[Class[_], AnyRef] = new HashMap[Class[_], AnyRef]

  def nextInt: Int = nextBigInt.intValue()

  def nextLong: Long = nextBigInt.longValue()

  def nextBigInteger: BigInteger = nextBigInt.bigInteger

  def nextBigInt: BigInt = PrefixFreeBigInt.prefixDecode(self)

  def nextInt(length: Int): Int = nextBigInt(length).intValue()

  def nextLong(length: Int): Long = nextBigInt(length).longValue()

  def nextBigInteger(length: Int): BigInteger = nextBigInt(length).bigInteger

  def nextBigInt(length: Int): BigInt = {
    var result: BigInt = 0
    var i = 0
    while (i < length) {
      val bit: Bit = PrefixFreeBigInt.readBit(self)
      result = result << 1
      if (bit.asInt > 0) {
        result = result.setBit(0)
      }
      i += 1
    }
    result
  }

  def nextString: String = {
    val length: Int = nextInt
    val buffer = new Array[Byte](length)
    var i = 0
    while (i < length) {
      buffer(i) = nextInt(8).asInstanceOf[Byte]
      i += 1
    }
    try {
      new String(buffer, 0, length, "UTF-8")
    } catch {
      case exception: UnsupportedEncodingException =>
        throw ExceptionWrapper.wrap(exception)
    }
  }

  def nextEnumConstant[E >: Null <: Enum[E]](constantType: Class[E]): E = {
    val constants: Array[E] = constantType.getEnumConstants
    val last = constants.length - 1
    if (last >= 0) {
      val bitLength: Int = BigInt.int2bigInt(last).bitLength
      val index: Int = nextInt(bitLength)
      return constants(index)
    }
    null
  }

  def nextBoolean: Boolean = nextInt(1) >= 1

  def addHelper[T <: AnyRef](helper: T, helperType: Class[T]): Unit = helpers = helpers + (helperType -> helper)

  def getHelper[T >: Null <: AnyRef](helperType: Class[T]): T = {
    helpers.get(helperType) match {
      case Some(x) => helperType.cast(x)
      case None => null
    }
  }
}
