package xyz.jmullin.ld34.desktop

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import xyz.jmullin.ld34.LD34

object Main extends App {
  val config = new LwjglApplicationConfiguration

  config.title = "LD34"
  config.width = 800
  config.height = 600

  new LwjglApplication(LD34, config)
}