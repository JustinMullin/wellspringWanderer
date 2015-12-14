package xyz.jmullin.ld34.entity

import java.util.concurrent.atomic.AtomicReference

import com.badlogic.gdx.graphics.GL20._
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, GL20, Texture}
import com.badlogic.gdx.{Gdx, Input}
import xyz.jmullin.drifter.FloatMath._
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.RandomUtil._
import xyz.jmullin.drifter.animation.Trigger._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.{Draw, ShaderSet, Shaders}
import xyz.jmullin.ld34.model.{Messages, TerrainGenerator, Terrain}
import xyz.jmullin.ld34.{Assets, MainScreen}

object Planet extends Entity2D {
  var terrain: Option[Terrain] = None

  var deathTextShown = false
  var died = false
  var plantedTextShown = false
  var planted = false
  var reachedHalfway = false
  var skipTutorial = false

  var firstLevel = true

  var populateFromTerrain: AtomicReference[Boolean] = new AtomicReference(false)

  def islands = terrain.get.islands
  def dirtColors = terrain.get.dirtColors
  def grassColors = terrain.get.grassColors
  def grassLength = terrain.get.grassLength

  var dirtTexture: Texture = _
  var normalTexture: Texture = _

  var resetCamera = false

  var cameraPosition = V2(gameW/2f, gameH+150)
  var cameraZoom = 1f

  var shipGlow = 0f

  val shader = new ShaderSet("map", "default") {
    def updateTextures(foliage: Texture, map: Texture)(implicit batch: SpriteBatch): Unit = {
      foliage.bind(4)
      map.bind(5)

      program.setUniformi("foliage", 4)
      program.setUniformi("map", 5)

      Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0)
    }
  }

  val atmosphereShader = new ShaderSet("atmosphere", "default") {
    def updateTextures(map: Texture)(implicit batch: SpriteBatch): Unit = {
      map.bind(5)

      program.setUniformi("map", 5)

      Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0)
    }
  }

  case class Island(v: V2, size: Float) {
    def metaball(o: V2) = size / (o - v).len()
    def normal(o: V2) = (o - v).nor()
  }

  var sprouted = 0
  var maxRoots = 0
  var atmosphere = 0f
  var displayAtmosphere = 0f
  var roots = List[Root]()

  var normalMap = Map[V2, V2]()
  var powerMap = Map[V2, Float]()

  var time = 0f

  var activeShip: Option[Ship] = None

  case class Root(v: V2, normal: V2) {
    def distance(o: V2) = (v - o).len()
  }

  def camera = MainScreen.world.camera

  /**
    * Called by the parent container on each frame to render this entity.
    *
    * @param batch Active SpriteBatch to use in rendering.
    */
  override def render(implicit batch: SpriteBatch): Unit = {
    implicit val _layer = layer.get

    Shaders.switch(shader)

    shader.program.setUniformf("time", time)
    activeShip.foreach { s=>
      shader.program.setUniformf("shipV", s.position)
      shader.program.setUniformf("thrustGlow", s.thrustVolume)
    }
    shader.program.setUniformf("shipGlow", shipGlow)
    shader.program.setUniformf("atmosphere", displayAtmosphere)

    shader.updateTextures(Foliage.grassFbo.getColorBufferTexture, normalTexture)

    Draw.texture(dirtTexture, V2(0, 0), gameSize)

    Shaders.switch(atmosphereShader)

    activeShip.foreach { s=>
      atmosphereShader.program.setUniformf("shipV", s.position)
      atmosphereShader.program.setUniformf("thrustGlow", s.thrustVolume)
    }
    atmosphereShader.program.setUniformf("shipGlow", shipGlow)
    atmosphereShader.program.setUniformf("atmosphere", displayAtmosphere)

    atmosphereShader.updateTextures(normalTexture)

    batch.setBlendFunction(GL_SRC_ALPHA, GL_ONE)

    Draw.texture(dirtTexture, V2(0, 0), gameSize)

    Shaders.switch(Shaders.default)

    batch.setBlendFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
  }

  /**
    * Called by the parent container at each tick to update this entity.
    *
    * @param delta Time in seconds elapsed since the last update tick.
    */
  override def update(implicit delta: Float): Unit = {
    time += delta

    displayAtmosphere += (atmosphere - displayAtmosphere)/10f

    if(resetCamera) {
      cameraPosition += (V2(gameW/2f, gameH+180) - cameraPosition) / 40f
      cameraZoom += (1f - cameraZoom) / 10f
    } else {
      activeShip foreach { ship =>
        val deltaFromCenter = ship.position-gameSize/2f
        cameraPosition += ((gameSize/2f + deltaFromCenter*0.8f) - cameraPosition) / 40f
        cameraZoom += (clamp(deltaFromCenter.len()/350f, 0.75f, 2f) - cameraZoom) / 10f
        shipGlow += (1f - shipGlow) / 40f
      }
    }

    if(activeShip.isEmpty) {
      shipGlow += (0f - shipGlow) / 40f
    }

    camera.position.set(cameraPosition, 0)
    camera.zoom = cameraZoom

    super.update
  }

  def plantAt(v: V2, color: Color, length: Float): Unit = {
    planted = true

    var toRemove = Set[Root]()
    roots.filter(_.distance(v) < 4f) foreach { root =>
      toRemove += root
      val blade = Grass(color, length, root, max(0.4f, 1f - (root.distance(v)/20f)))

      add(blade)
      Foliage.grassBlades += blade
    }
    roots = roots.filterNot(toRemove.contains)
  }

  def incrementAtmosphere(): Unit ={
    sprouted += 1
    atmosphere = 2.5f*sprouted/maxRoots.toFloat
    if(atmosphere >= 1.0f) {
      atmosphere = 1f
    }
    Assets.theme.setVolume(atmosphere*atmosphere)
  }

  def reset(quick: Boolean=false): Unit = {
    println("Starting to reset, spinning 'til terrain is ready...")
    while(!populateFromTerrain.get()) true
    println("Populating terrain")
    populateFromTerrain.set(false)

    TerrainGenerator.pregenerate(9)

    Foliage.grassBlades = Set()
    Foliage.grassBlades.foreach(_.remove())

    maxRoots = 0
    sprouted = 0
    atmosphere = 0
    displayAtmosphere = 0
    roots = List()
    Foliage.needsClear = true
    Assets.theme.setVolume(0f)

    normalTexture = new Texture(terrain.get.normalPixmap)
    dirtTexture = new Texture(terrain.get.dirtPixmap)
    powerMap = terrain.get.powerMap
    normalMap = terrain.get.normalMap
    roots = terrain.get.roots
    maxRoots = terrain.get.maxRoots

    MainScreen.world.children.foreach {
      case s: Seed => s.dead = true; s.remove();
      case s: Ship => Assets.thrust.stop(); s.remove()
      case s: ShipPart => s.remove()
      case _ => Unit
    }

    if(!MainScreen.introTextShown && !Gdx.input.isKeyPressed(Input.Keys.SPACE) && !skipTutorial) {
      delay(3f) {
        MainScreen.messageBox.showMessage("Space has long been asleep. It is time we did something about this.")
        delay(5f) {
          tween(1f) { n=>
            MainScreen.messageBox.alpha = 1-n
          } go()
          delay(2f) {
            MainScreen.messageBox.showMessage("Direct our seed pods towards the planetoid below. Bring life to a dead world.")
            delay(6f) {
              tween(1f) { n=>
                MainScreen.messageBox.alpha = 1-n
              } go()
              delay(2f) {
                MainScreen.messageBox.showMessage("Please be careful with the pods. They require a gentle landing.")
                delay(6f) {
                  tween(1f) { n=>
                    MainScreen.messageBox.alpha = 1-n
                  } go()
                  respawn(2.5f)
                } go()
              } go()
            } go()
          } go()
        } go()
      } go()
      MainScreen.introTextShown = true
    } else {
      respawn(2.5f)
    }
  }

  def respawn(time: Float = 2.5f): Unit = {
    activeShip = None
    delay(time) {
      if(died && !deathTextShown && !Gdx.input.isKeyPressed(Input.Keys.SPACE) && !skipTutorial) {
        delay(1f) {
          MainScreen.messageBox.showMessage("Please do not worry. No life was lost, and we have many other pods.")
          delay(5f) {
            tween(1f) { n=>
              MainScreen.messageBox.alpha = 1-n
            } go()
            delay(2f) {
              MainScreen.messageBox.showMessage("Try again. More gently. More carefully.")
              delay(4f) {
                tween(1f) { n=>
                  MainScreen.messageBox.alpha = 1-n
                } go()
                delay(2.5f) {
                  next()
                } go()
              } go()
            } go()
          } go()
        } go()
        deathTextShown = true
      } else if(atmosphere >= 0.5f && !reachedHalfway && !skipTutorial) {
        reachedHalfway = true

        delay(1f) {
          MainScreen.messageBox.showMessage("We are coming close now. Can you feel it? The planetoid hums with energy.")
          delay(6f) {
            tween(1f) { n=>
              MainScreen.messageBox.alpha = 1-n
            } go()
            delay(2f) {
              MainScreen.messageBox.showMessage("Soon our work here will be complete.")
              delay(5f) {
                tween(1f) { n=>
                  MainScreen.messageBox.alpha = 1-n
                } go()
                resetCamera = false
                delay(2.5f) {
                  next()
                } go()
              } go()
            } go()
          } go()
        } go()
      } else if(atmosphere >= 0.995f) {
        resetCamera = true

        if(firstLevel) {
          firstLevel = false
          delay(1f) {
            MainScreen.messageBox.showMessage("Good. Very good. See how we improve this world?")
            delay(5f) {
              tween(1f) { n=>
                MainScreen.messageBox.alpha = 1-n
              } go()
              delay(2f) {
                MainScreen.messageBox.showMessage("There are many more like it. Come, let us move on to another place.")
                delay(6f) {
                  tween(1f) { n=>
                    MainScreen.messageBox.alpha = 1-n
                  } go()
                  resetCamera = false
                  delay(2.5f) {
                    nextLevel()
                  } go()
                } go()
              } go()
            } go()
          } go()
        } else {
          resetCamera = true
          val message = Messages.getLevelCompleteMessage
          delay(1f) {
            MainScreen.messageBox.showMessage(message)
            delay(6f) {
              tween(1f) { n=>
                MainScreen.messageBox.alpha = 1-n
              } go()
              resetCamera = false
              delay(2.5f) {
                nextLevel()
              } go()
            } go()
          } go()
        }
      } else if(planted && !plantedTextShown && !Gdx.input.isKeyPressed(Input.Keys.SPACE) && !skipTutorial) {
        delay(1f) {
          MainScreen.messageBox.showMessage("Yes, yes. Just like that. Seeds beget plants. Plants are life.")
          delay(5f) {
            tween(1f) { n=>
              MainScreen.messageBox.alpha = 1-n
            } go()
            delay(2f) {
              MainScreen.messageBox.showMessage("This is right. This is good. Please continue.")
              delay(4f) {
                tween(1f) { n=>
                  MainScreen.messageBox.alpha = 1-n
                } go()
                delay(2.5f) {
                  next()
                } go()
              } go()
            } go()
          } go()
        } go()
        plantedTextShown = true
      } else {
        next()
      }
    } go()
  }

  def nextLevel(): Unit = {
    Warp.play()
    delay(1.5f) {
      reset()
    } go()
  }

  def next(): Unit = {
    newShip()
  }

  def newShip(): Unit = {
    val ship = new Ship
    activeShip = Some(ship)
    MainScreen.world.add(ship)

    Assets.launch.play(rFloat(0.4f, 0.5f), rFloat(0.6f, 0.7f), rFloat(-0.1f, 0.1f))

    ship.position.set(V2(gameW/2f, gameH+180))
    ship.velocity.set(0, 0)
    ship.rotation = 90f
    ship.rotationVelocity = 0
  }

  override def containsPoint(v: V2): Boolean = true

  override def keyDown(keycode: Int): Boolean = {
    if(keycode == Input.Keys.SPACE) {
      //reset()
    }

    super.keyDown(keycode)
  }
}
