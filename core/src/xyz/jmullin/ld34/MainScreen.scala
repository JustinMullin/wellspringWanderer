package xyz.jmullin.ld34

import com.badlogic.gdx.graphics.Color
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.{DrifterScreen, Shaders}
import xyz.jmullin.ld34.entity._
import xyz.jmullin.ld34.model.TerrainGenerator

object MainScreen extends DrifterScreen(Color.BLACK) {
  val ui = newLayer2D(gameSize, autoCenter = true)
  val world = newLayer2D(gameSize, autoCenter = true, Shaders.default)
  val background = newLayer2D(gameSize, autoCenter = true)

  var introTextShown = false

  background.add(Stars)

  ui.add(Warp)

  world.add(Mothership)
  world.add(Title)
  Title.position.set(V2(gameW/2f, gameH+380))
  Mothership.position.set(V2(gameW/2f, gameH+180))

  println(Planet)
  TerrainGenerator.pregenerate(9)

  def startGame(): Unit = {
    //ui.add(AtmosphereDisplay)
    background.add(Foliage)
    world.add(Planet)

    Assets.theme.setLooping(true)
    Assets.theme.setVolume(0f)
    Assets.theme.play()

    Planet.reset(true)
  }

  val messageBox = new MessageBox(V2(1, -1), Assets.milleniaSmall, Color.WHITE)
  ui.add(messageBox)
  messageBox.position.set(V2(gameW/2f-200, gameH/2f-150))
  messageBox.size.set(V2(400, 300))
  messageBox.showMessage("")

  override def keyDown(keycode: Int): Boolean = {
    keycode match {
      //case Keys.ESCAPE => Gdx.app.exit()
      case _ => Unit
    }

    super.keyDown(keycode)
  }
}