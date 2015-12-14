package xyz.jmullin.ld34

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import xyz.jmullin.drifter.GdxAlias._

object FboPool {
  private var pool = Map[String, FrameBuffer]()

  def get(name: String, format: Pixmap.Format) = {
    val fbo = pool.getOrElse(name, {
      val newFbo = new FrameBuffer(format, gameW, gameH, false)
      pool += name -> newFbo
      newFbo
    })
    fbo
  }
}
