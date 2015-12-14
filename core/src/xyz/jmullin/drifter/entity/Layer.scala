package xyz.jmullin.drifter.entity

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.viewport.Viewport
import xyz.jmullin.drifter.enrich.RichGeometry
import xyz.jmullin.drifter.enrich.RichGeometry._

/**
 * Abstracts a renderable layer of the screen with some viewport.  Generally layers will create
 * entities as children to update and render.  A layer can be hidden (visible=false, in which case
 * render will not be performed) and/or inactive (active=false, in which case update will not be
 * performed).
 */
trait Layer extends InputProcessor {
  val viewportSize: V2
  val autoCenter: Boolean
  var viewport: Viewport = _

  var visible = true
  var active = true

  def update(delta: Float): Unit
  def render(): Unit
  def dispose(): Unit = {}
}