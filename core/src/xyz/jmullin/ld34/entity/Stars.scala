package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.animation.Trigger._
import xyz.jmullin.drifter.entity.{EntityContainer2D, Entity2D}
import xyz.jmullin.ld34.{MainScreen, Assets}

object Stars extends Entity2D {
  depth = 100
  var initialized = false

  def camera = MainScreen.world.camera

  override def create(container: EntityContainer2D): Unit = {
    Assets.noise.setLooping(true)
    Assets.noise.setVolume(0.65f)
    Assets.noise.play()

    delay(3f) {
      Title.on = true
    } go()
    delay(5f) {
      Title.secondOn = true
    } go()
    delay(7.5f) {
      Title.thirdOn = true
    } go()
    delay(3f) {
      Assets.intro.play()
    } go()
  }

  override def render(implicit batch: SpriteBatch): Unit = {
    Draw.texture(Assets.stars, V2(0, 0), gameSize)
  }

  override def update(implicit delta: Float): Unit = {
    if(!initialized) {
      camera.position.set(V2(gameW/2f, gameH+180), 0)
      camera.zoom = 1f
      camera.update()
      initialized = true
    }

    super.update
  }
}
