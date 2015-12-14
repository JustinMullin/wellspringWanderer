package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.RandomUtil._
import xyz.jmullin.drifter.enrich.RichColor._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.ld34.Assets
import xyz.jmullin.ld34.entity.Planet.Root

case class Grass(_color: Color, avgLength: Float, root: Root, sizeMultiplier: Float) extends Entity2D {

  val v = root.v.cpy()
  val normal = root.normal.cpy()
  val curl = rFloat(-0.5f, 0.5f)

  val length = (avgLength+rFloat(-4f, 4f)) * sizeMultiplier
  val color = _color.cpy().mul(rFloat(0.8f, 1.5f)).alpha(0.2f)

  var delay = rFloat(1.4f, 3.0f)
  val maxLife = rFloat(4.5f, 6.5f)
  var life = maxLife

  override def render(implicit batch: SpriteBatch): Unit = {
    if(delay <= 0f) {
      Assets.placeholder.setColor(color)
      Draw.sprite(Assets.placeholder, v, V2(1.2f, 1.2f))
    }
  }

  override def update(implicit delta: Float): Unit = {
    if(delay > 0f) {
      delay -= delta
    } else {
      v.set(v + (normal*delta*length)/maxLife)
      normal.rotate(curl + rFloat(-0.2f, 0.2f))

      life -= delta
      if(life <= delta) {
        Planet.incrementAtmosphere()
        remove()
      }
    }
  }
}
