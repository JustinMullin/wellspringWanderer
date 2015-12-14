package xyz.jmullin.ld34

import com.badlogic.gdx.Game

object LD34 extends Game {
  override def create(): Unit = {
    Assets.load()
    Assets.finishLoading()
    Assets.populate()

    setScreen(MainScreen)
  }
}
