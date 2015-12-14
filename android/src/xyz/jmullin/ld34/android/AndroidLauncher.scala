package xyz.jmullin.ld34.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.{AndroidApplication, AndroidApplicationConfiguration}
import xyz.jmullin.ld34.LD34

class AndroidLauncher extends AndroidApplication {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration

    initialize(LD34, config)
  }
}
