package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.FloatMath._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.enrich.RichColor._
import xyz.jmullin.ld34.Assets

object Warp extends Entity2D {
  var time = 0f
  var playing = false

  override def render(implicit batch: SpriteBatch): Unit = {
    if(playing && time > 0f) {
      Assets.fill.setColor(warpColor)
      Draw.sprite(Assets.fill, V2(0, 0), gameSize)
    }
  }

  def ramp = sin((time/3f)*Pi)
  def warpColor = Ca(ramp, ramp, 0.5f+ramp*0.5f, pow(ramp, 6f))

  override def update(implicit delta: Float): Unit = {
    if(playing) {
      time += delta

      if(time > 3f) {
        playing = false
        time = 0f
      }
    }
  }

  def play(): Unit = {
    time = 0f
    Assets.warp.play(1f)
    playing = true
  }
}
