package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.ld34.Assets

class Seed(color: Color, length: Float) extends Entity2D {
  val velocity = V2(0, 0)
  val gravity = 3f
  depth = -10

  var dead = false

  override def render(implicit batch: SpriteBatch): Unit = {
    Assets.placeholder.setColor(color)
    Draw.sprite(Assets.placeholder, position-V2(1f, 1f), V2(2f, 2f))
  }

  def gridPos = V2(position.x.toInt, gameH - position.y.toInt)

  override def update(implicit delta: Float): Unit = {
    if(!dead) {
      val normal = Planet.normalMap.getOrElse(gridPos, V2(0, 0))
      velocity.add(normal*gravity)

      position.add(velocity)
      velocity.scl(0.99f)

      if(Planet.powerMap.get(gridPos).forall(_ >= 1f)) {
        Planet.plantAt(position.flipY, color, length)
        remove()
      }
    } else {
      remove()
    }
  }
}
