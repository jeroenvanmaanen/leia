package org.leialearns.logic.utilities

trait Bit {
  def asInt: Int
  def asBoolean: Boolean
}
case object ZERO extends Bit {
  def asInt = 0
  def asBoolean = false
}
case object ONE extends Bit {
  def asInt = 1
  def asBoolean = true
}
