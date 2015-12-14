package xyz.jmullin.ld34.entity

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{Gdx, Input}
import xyz.jmullin.drifter.FloatMath._
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.RandomUtil._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.drifter.entity.Entity2D
import xyz.jmullin.drifter.{Draw, ShaderSet, Shaders}
import xyz.jmullin.ld34.model.TerrainGenerator
import xyz.jmullin.ld34.{Assets, MainScreen}

object Ship {
  lazy val scale = V2(Assets.ship.getWidth*0.5f, Assets.ship.getHeight*0.5f)
  val thrustShader = new ShaderSet("thrust", "default")
}

class Ship extends Entity2D {
  val colors = TerrainGenerator.newGrassColors
  val length = TerrainGenerator.newGrassLength

  val turnSpeed = 6f
  val thrustSpeed = 1f
  val gravity = 0.6f

  var seeds = 30
  val seedDelay = 0.1f
  var seedTimer = -1f

  var thrustVolume = 0f
  var thrustSoundId: Option[Long] = None

  var rotationVelocity = 0f
  val velocity = V2(0, 0)

  depth = 0

  var rotation = 0f
  var landed = false

  size.set(Ship.scale)

  /*
  10, 11
  14, 6
  15, 3
  3, 0
   */

  override def render(implicit batch: SpriteBatch): Unit = {
    implicit val _layer = layer.get

    Assets.ship.setCenter(size.x/2f, size.y/2f)
    Assets.ship.setOriginCenter()
    Assets.ship.setRotation(rotation-90f)
    Draw.sprite(Assets.ship, position-size/2f, size)

    Shaders.switch(Ship.thrustShader)
    Ship.thrustShader.program.setUniformf("thrust", thrustVolume)
    var center = position+V2(8f, 0).rotate(rotation+135f)
    Ship.thrustShader.program.setUniformf("thrustCenter", center)
    Draw.sprite(Assets.placeholder, center-V2(16, 16), V2(32f, 32f))
    batch.flush()
    center = position+V2(8f, 0).rotate(rotation+215f)
    Ship.thrustShader.program.setUniformf("thrustCenter", center)
    Draw.sprite(Assets.placeholder, center-V2(16, 16), V2(32f, 32f))
    Shaders.switch(Shaders.default)
  }

  def gridPos(v: V2 = position) = V2(v.x.toInt, gameH - v.y.toInt).clamp(V2(4f, 4f), gameSize-4f)

  override def update(implicit delta: Float): Unit = {
    val normal = Planet.normalMap.getOrElse(gridPos(), V2(0, 0)).cpy()

    if(!landed) {
      velocity.add(normal.cpy().limit(0.01f)*gravity)

      if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        rotationVelocity += turnSpeed*delta
      }
      if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        rotationVelocity -= turnSpeed*delta
      }
      if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
        velocity.add(V2(thrustSpeed*delta, 0f).rotate(rotation))

        if(thrustVolume <= 0.01f) {
          thrustSoundId = Some(Assets.thrust.loop(thrustVolume))
        }
        thrustVolume += (1f-thrustVolume)/25f
      } else if(thrustVolume > 0.01f) {
        reduceThrustNoise()
      }

      position.add(velocity)
      rotation += rotationVelocity

      rotationVelocity *= 0.99f

      val surfaceNormal = normal.cpy().nor()
      if(Planet.powerMap.get(gridPos(position + surfaceNormal*size.minComponent/2.8f)).exists(_ >= 1f)) {
        if(V2(1, 0).rotate(rotation).dot(surfaceNormal) > -0.9f) {
          die()
        } else if(velocity.len() > 0.5f) {
          die()
        } else {
          landed = true
          Assets.land.play(rFloat(0.6f, 0.7f), rFloat(0.6f, 0.7f), rFloat(-0.1f, 0.1f))
          Planet.respawn()
        }
      }
    } else {
      if(seeds > 0) {
        if(seedTimer > seedDelay) {
          Assets.drop.play(rFloat(0.05f, 0.1f), rFloat(0.1f, 0.2f), rFloat(-0.2f, 0.2f))
        }
        while(seedTimer > seedDelay) {
          val seed = new Seed(rElement(colors), length)
          seed.position.set(position + V2(rFloat(0f, 3f), 0).rotate(rFloat(360f)))
          seed.velocity.set(V2(-rFloat(2f, 4f), 0).rotate(rotation+180+rFloat(-35f, 35f)))
          MainScreen.world.add(seed)

          seeds -= 1
          seedTimer -= seedDelay
        }
        seedTimer += delta
      }

      if(thrustVolume > 0.01f) {
        reduceThrustNoise()
      }

      val landingAngle = normal.cpy().inverse.angle
      while(rotation < 0) rotation += 360f
      rotation = rotation % 360f

      if(rotation < landingAngle) {
        if(abs(rotation - landingAngle) < 180) rotation += (landingAngle - rotation) / 10f
        else rotation += (rotation - landingAngle + 360f) / 10f
      } else {
        if(abs(rotation - landingAngle) < 180) rotation += (landingAngle - rotation) / 10f
        else rotation += (rotation - landingAngle + 360f) / 10f
      }
    }

    thrustSoundId.foreach { s =>
      Assets.thrust.setVolume(s, thrustVolume)
      Assets.thrust.setPitch(s, rFloat(0.5f, 1.0f))
    }

    if(position.x < -width-1f-200f) velocity.add(V2(0.1f, 0))
    if(position.x > gameW+width+1+200f) velocity.add(V2(-0.1f, 0))
    if(position.y < -height-1f-200f) velocity.add(V2(0, 0.1f))
    if(position.y > gameH+height+1+200f) velocity.add(V2(0, -0.1f))
  }

  def die(): Unit = {
    Planet.died = true

    thrustSoundId.foreach(Assets.thrust.stop)
    Planet.respawn()
    remove()

    val explosion = new Explosion
    explosion.position.set(position)
    MainScreen.world.add(explosion)

    Assets.explosion.play(rFloat(0.35f, 0.5f), rFloat(0.6f, 0.8f), rFloat(-0.1f, 0.1f))

    val parts = Map(
      Assets.partA -> V2(10, 11),
      Assets.partB -> V2(14, 6),
      Assets.partC -> V2(15, 3),
      Assets.partD -> V2(3, 0))

    for((sprite, offset) <- parts) {
      val part = new ShipPart(sprite)
      part.position.set(position + (offset/2f+V2(sprite.getWidth, sprite.getHeight)/2f - size/2f).rotate(rotation))
      part.rotation = 0
      part.velocity = V2(0, 0)
      part.rotationVelocity = 0
      MainScreen.world.add(part)
    }
  }

  def reduceThrustNoise(): Unit = {
    thrustVolume -= thrustVolume/10f
    if(thrustVolume <= 0.01f) {
      Assets.thrust.stop()
      thrustSoundId = None
    }
  }
}
