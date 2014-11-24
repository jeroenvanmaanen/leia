package org.leialearns.logic.utilities

import java.io.Reader

class PrefixDecoderWrapper(reader: Reader) extends Reader with PrefixDecoderTrait{
  override def read(cbuf: Array[Char], off: Int, len: Int): Int = reader.read(cbuf, off, len)
  override def close(): Unit = reader.close()
}
