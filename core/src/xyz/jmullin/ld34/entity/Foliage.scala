package xyz.jmullin.ld34.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.ld34.FboPool

object Foliage extends Entity2D {
  var grassFbo: FrameBuffer = _

  var grassBlades = Set[Grass]()
  var needsClear = true

  /**
    * Called by the parent container at each tick to update this entity.
    *
    * @param delta Time in seconds elapsed since the last update tick.
    */
  override def update(implicit delta: Float): Unit = {
    grassBlades = grassBlades.filter(_.parent.isDefined)
  }

  /**
    * Called by the parent container on each frame to render this entity.
    *
    * @param batch Active SpriteBatch to use in rendering.
    */
  override def render(implicit batch: SpriteBatch): Unit = {
    batch.flush()

    grassFbo = FboPool.get("grass", Format.RGBA8888)

    grassFbo.begin()

    if(needsClear) {
      Gdx.gl.glClearColor(0, 0, 0, 0)
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)
      needsClear = false
    }

    batch.flush()

    grassBlades.foreach(_.render(batch))

    batch.flush()
    grassFbo.end()

    batch.flush()
  }
}
