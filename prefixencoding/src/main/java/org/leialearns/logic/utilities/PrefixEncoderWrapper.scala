package org.leialearns.logic.utilities

import java.io.Writer

class PrefixEncoderWrapper(writer: Writer) extends Writer with PrefixEncoderTrait {
  override def write(cbuf: Array[Char], off: Int, len: Int): Unit = writer.write(cbuf, off, len)
  override def flush(): Unit = writer.flush()
  override def close(): Unit = writer.close()
}
