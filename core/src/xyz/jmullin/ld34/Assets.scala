package xyz.jmullin.ld34

import com.badlogic.gdx.audio.{Music, Sound}
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite, TextureAtlas}
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import xyz.jmullin.drifter.assets.DrifterAssets

object Assets extends DrifterAssets {
  var ld34: TextureAtlas = _

  var petMe12: BitmapFont = _
  var petMe20: BitmapFont = _
  var millenia: BitmapFont = _
  var milleniaSmall: BitmapFont = _

  var intro: Sound = _

  var messageA: Sound = _
  var messageB: Sound = _
  var messageC: Sound = _
  var messageD: Sound = _

  var warp: Sound = _

  var thrust: Sound = _
  var drop: Sound = _
  var explosion: Sound = _
  var land: Sound = _
  var launch: Sound = _

  var theme: Music = _
  var noise: Music = _

  var placeholder: Sprite = _
  var ship: Sprite = _
  var mothership: Sprite = _

  var partA: Sprite = _
  var partB: Sprite = _
  var partC: Sprite = _
  var partD: Sprite = _

  var background: Texture = _
  var stars: Texture = _
  var instructions: Texture = _

  val pixmap = new Pixmap(1, 1, Format.RGBA8888)
  pixmap.setColor(Color.WHITE)
  pixmap.fill()
  lazy val fill: Sprite = new Sprite(new Texture(pixmap))
}