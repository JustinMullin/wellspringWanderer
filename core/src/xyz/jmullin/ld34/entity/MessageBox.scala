package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, SpriteBatch}
import xyz.jmullin.drifter.Draw
import xyz.jmullin.drifter.enrich.RichColor._
import xyz.jmullin.drifter.RandomUtil._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.ld34.Assets

case class MessageTiming(normal: Float=0.04f, halfStop: Float=0.2f, fullStop: Float=0.4f)

class MessageBox(align: V2, font: BitmapFont, var color: Color, margin: Int=4, lineHeightMult: Float=1.2f, timing: MessageTiming=MessageTiming()) extends Entity2D {
  val sounds = rand.shuffle(List(Assets.messageA, Assets.messageB, Assets.messageC, Assets.messageD))
  var messageId = 0

  var messageLines = Vector[String]()

  var totalCharacters = 0
  var shown = 0
  var delay = 0f
  var done = false

  depth = -100

  var alpha = 1f

  var layout = new GlyphLayout()

  def characterDelay(char: Char) = char match {
    case '.' | ';' | '?' => timing.fullStop
    case ',' | ':' => timing.halfStop
    case _ => timing.normal
  }

  var messageSoFar = Vector[String]()

  def updateMessageSoFar(): Unit = {
    var i = shown

    messageSoFar = for(line <- messageLines) yield {
      val part = line.take(i)
      i -= line.length
      part
    }
  }

  def lineHeight = font.getLineHeight*lineHeightMult

  def showMessage(newMessage: String): Unit = {
    if(newMessage.size > 0) {
      val messageSound = sounds(messageId % sounds.size)
      messageSound.play(rFloat(0.7f, 1f))
      messageId += 1
    }

    alpha = 1f
    shown = 0
    done = false
    delay = 0
    totalCharacters = 0
    var tempString = ""
    messageLines = Vector()

    for(word <- newMessage.split(" +")) {
      layout.setText(font, tempString + word)
      if(layout.width+margin*2 > size.x) {
        messageLines :+= tempString + " "
        totalCharacters += tempString.length+1
        tempString = word + " "
      } else {
        tempString += word + " "
      }
    }

    messageLines :+= tempString
    totalCharacters += tempString.length
  }

  override def render(implicit batch: SpriteBatch): Unit = {
    for((line, i) <- messageSoFar.zipWithIndex) {
      font.setColor(Ca(color.r, color.g, color.b, alpha))
      Draw.string(line, position+V2(margin, size.y-margin-lineHeight*i), font, align)
    }
  }

  override def update(implicit delta: Float): Unit = {
    if(!done) {
      delay -= delta
      while(delay <= 0 && !done) nextChar()
    }
  }

  def nextChar(): Unit = {
    alpha = 1f
    shown += 1
    updateMessageSoFar()
    val next = messageSoFar.filter(_.length > 0).last.last

    if(shown < totalCharacters) {
      delay = characterDelay(next)
    } else {
      done = true
    }
  }
}