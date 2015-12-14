package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.entity.{EntityContainer2D, Entity2D}
import xyz.jmullin.ld34.Assets

object Mothership extends Entity2D {
  var rotation = 0f

  override def create(container: EntityContainer2D): Unit = {
    size.set(Assets.mothership.getWidth, Assets.mothership.getHeight)
  }

  override def render(implicit batch: SpriteBatch): Unit = {
    Assets.mothership.setOriginCenter()
    Assets.mothership.setRotation(rotation)
    Draw.sprite(Assets.mothership, position-size/2f, size)
    Assets.mothership.setRotation(-rotation)
    //Draw.sprite(Assets.mothership, position-(size/2f)*0.7f, size*0.7f)
  }

  override def update(implicit delta: Float): Unit = {
    rotation += delta*10f
  }
}
