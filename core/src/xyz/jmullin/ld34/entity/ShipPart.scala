package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.entity.Entity2D

class ShipPart(sprite: Sprite) extends Entity2D {
  val gravity = 0.6f

  depth = 5
  var alpha = 0f

  var landed = false
  var rotation = 0f
  var rotationVelocity = 0f
  var velocity = V2(0, 0)

  size.set(Ship.scale)
  override def render(implicit batch: SpriteBatch): Unit = {
    sprite.setCenter(sprite.getWidth/4f, sprite.getHeight/4f)
    sprite.setOriginCenter()
    sprite.setRotation(rotation-90f)
    sprite.setAlpha(alpha)
    Draw.sprite(sprite, position-size/4f, size/2f)
  }

  def gridPos(v: V2 = position) = V2(v.x.toInt, gameH - v.y.toInt).clamp(V2(4f, 4f), gameSize-4f)

  override def update(implicit delta: Float): Unit = {
    alpha += (1f-alpha)/30f

    val normal = Planet.normalMap.getOrElse(gridPos(), V2(0, 0)).cpy()

    if(!landed) {
      velocity.add(normal.cpy().limit(0.01f)*gravity)

      position.add(velocity)
      rotation += rotationVelocity

      rotationVelocity *= 0.99f

      val surfaceNormal = normal.cpy().nor()
      if(Planet.powerMap.get(gridPos(position + surfaceNormal*size.minComponent/2.8f)).exists(_ >= 1f)) {
        landed = true
      }
    }

    if(position.x < -width-1f-200f) velocity.add(V2(0.1f, 0))
    if(position.x > gameW+width+1+200f) velocity.add(V2(-0.1f, 0))
    if(position.y < -height-1f-200f) velocity.add(V2(0, 0.1f))
    if(position.y > gameH+height+1+200f) velocity.add(V2(0, -0.1f))
  }
}
