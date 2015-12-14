package xyz.jmullin.ld34.entity

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.enrich.RichColor._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.ld34.{MainScreen, Assets}

object Title extends Entity2D {
  var on = false
  var alpha = 0f
  var secondOn = false
  var secondAlpha = 0f
  var thirdOn = false
  var thirdAlpha = 0f

  var playing = false

  override def render(implicit batch: SpriteBatch): Unit = {
    Assets.millenia.setColor(Ca(1, 1, 1, alpha))
    Draw.string("Wellspring", position - V2(25, 0), Assets.millenia, V2(0, 1))
    Draw.string("Wanderer", position + V2(25, -50), Assets.millenia, V2(0, 1))

    Assets.milleniaSmall.setColor(Ca(1, 1, 1, secondAlpha))
    Draw.string("a game by Justin Mullin", position + V2(50, -80), Assets.milleniaSmall, V2(0, 1))

    batch.setColor(Ca(1, 1, 1, thirdAlpha))
    Draw.texture(Assets.instructions, V2(0, gameH+180 - gameH/2f), gameSize)
    batch.setColor(Color.WHITE)
  }

  override def update(implicit delta: Float): Unit = {
    if(on && !playing) {
      alpha += (1f - alpha) / 30f
    } else {
      alpha += (0f - alpha) / 30f
    }
    if(secondOn && !playing) {
      secondAlpha += (1f - secondAlpha) / 30f
    } else {
      secondAlpha += (0f - secondAlpha) / 30f
    }
    if(thirdOn && !playing) {
      thirdAlpha += (1f - thirdAlpha) / 50f
    } else {
      thirdAlpha += (0f - thirdAlpha) / 30f
    }
  }

  override def keyDown(keycode: Int): Boolean = {
    if(!playing && keycode == Input.Keys.DOWN) {
      playing = true

      MainScreen.startGame()
    }

    true
  }
}
