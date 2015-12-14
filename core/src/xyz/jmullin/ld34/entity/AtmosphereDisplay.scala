package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.enrich.RichColor._
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.ld34.Assets

object AtmosphereDisplay extends Entity2D {
  var alpha = 0f

  lazy val font = Assets.petMe20

  override def render(implicit batch: SpriteBatch): Unit = {
    font.setColor(Ca(1, 1, 1, alpha))
    Draw.string(f"Atmosphere: ${(Planet.atmosphere*100f).toInt}%%", V2(gameW/2f, 5), font, V2(0, 1))
  }

  override def update(implicit delta: Float): Unit = {
    alpha += (1f-alpha)/10f
  }
}
