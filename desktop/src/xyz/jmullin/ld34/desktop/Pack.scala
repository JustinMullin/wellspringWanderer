package xyz.jmullin.ld34.desktop

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.tools.texturepacker.TexturePacker

object Pack extends App {
  val resourcePath = "image"

  val settings = new TexturePacker.Settings()
  settings.maxWidth = 2048
  settings.maxHeight = 2048
  settings.filterMin = Texture.TextureFilter.Nearest
  settings.filterMag = Texture.TextureFilter.Nearest
  settings.paddingX = 1
  settings.paddingY = 1

  TexturePacker.process(settings, resourcePath, "atlas", "ld34")
}