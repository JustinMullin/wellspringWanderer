package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.GL20._
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.{ShaderSet, Shaders, Draw}
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.ld34.{MainScreen, Assets}

object ExplosionDebug extends Entity2D {
  override def containsPoint(v: V2): Boolean = true

  override def touchDown(v: V2, pointer: Int, button: Int): Boolean = {
    val explosion = new Explosion
    explosion.position.set(v)
    MainScreen.world.add(explosion)

    true
  }
}

object Explosion {
  val shader = new ShaderSet("explosion", "default")
}

class Explosion extends Entity2D {
  var time = 0f
  var maxLife = 3f

  depth = 10

  size.set(V2(128, 128))

  override def render(implicit batch: SpriteBatch): Unit = {
    implicit val _layer = layer.get

    Shaders.switch(Explosion.shader)

    batch.enableBlending()
    batch.setBlendFunction(GL_SRC_ALPHA, GL_ONE)

    Explosion.shader.program.setUniformf("center", position)
    Explosion.shader.program.setUniformf("time", time/maxLife)
    Draw.sprite(Assets.fill, position - size/2f, size)
    Shaders.switch(Shaders.default)

    batch.setBlendFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
  }

  override def update(implicit delta: Float): Unit = {
    time += delta
    if(time >= maxLife) remove()
  }
}
