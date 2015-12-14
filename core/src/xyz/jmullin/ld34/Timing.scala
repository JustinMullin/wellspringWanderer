package xyz.jmullin.ld34

object Timing {
  var start = 0l

  def init(): Unit = {
    start = System.currentTimeMillis()
  }

  def elapsed(msg: String) = {
    println(s"$msg: ${System.currentTimeMillis() - start}ms")
    start = System.currentTimeMillis()
  }
}
