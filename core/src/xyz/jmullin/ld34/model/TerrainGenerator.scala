package xyz.jmullin.ld34.model

import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.{Pixmap, Color}
import xyz.jmullin.drifter.FloatMath._
import xyz.jmullin.drifter.GdxAlias._
import xyz.jmullin.drifter.RandomUtil._
import xyz.jmullin.drifter.enrich.RichColor._
import xyz.jmullin.drifter.enrich.RichGeometry._
import xyz.jmullin.ld34.entity.Planet.{Island, Root}
import xyz.jmullin.ld34.entity.Planet

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case class Terrain(
  islands: Seq[Island],
  dirtPixmap: Pixmap,
  normalPixmap: Pixmap,
  powerMap: Map[V2, Float],
  normalMap: Map[V2, V2],
  roots: List[Root],
  maxRoots: Int,
  dirtColors: List[Color],
  grassColors: List[Color],
  grassLength: Float)

object TerrainGenerator {

  def generateIslands(numIslands: Int) = for(i <- 0 until numIslands) yield newIsland(max(12f, min(16f, i.toFloat*2)))

  def generateDirtTexture(islands: List[Island], dirtColors: List[Color]) = {
    val pixmap = new Pixmap(gameW, gameH, Format.RGBA8888)
    pixmap.setColor(0, 0, 0, 0f)
    pixmap.fill()

    var powerMap = Map[V2, Float]()

    val a = rFloat(0.1f, 0.5f)
    val b = rFloat(0.1f, 0.5f)
    val c = rFloat(0.1f, 0.5f)

    for(x <- 0 until gameW; y <- 0 until gameH; v = V2(x, y)) {
      val power = islands.map(_.metaball(v)).sum

      if(power >= 1.0f) {
        val edge = 1f - max(0f, 2f - power)

        val color = dirtColors((1f+sin(y/4/a)*sin(x/4/b) + 1f+cos(y/4/c)).toInt%dirtColors.size).cpy()
        color.mul(0.5f+edge*0.5f, 0.5f+edge*0.5f, 0.5f+edge*0.5f, 1f)
        pixmap.drawPixel(x, y, Color.rgba8888(color.lerp(C(0.69f, 0.49f, 0.36f), clamp((power-1f)/0.2f, 0, 0.5f))))
      }

      powerMap += (v -> power)
    }

    (pixmap, powerMap)
  }

  def generateNormals(powerMap: Map[V2, Float]) = {
    val pixmap = new Pixmap(gameW, gameH, Format.RGB888)
    pixmap.setColor(1f, 1f, 1f, 0f)
    pixmap.fill()

    var roots = List[Root]()
    var normalMap = Map[V2, V2]()

    for(x <- 3 until gameW-3; y <- 3 until gameH-3; v = V2(x, y)) {
      val power = powerMap(v)
      val gradient = V2(
        powerMap(v+V2(3, 0)) - powerMap(v-V2(3, 0)),
        powerMap(v-V2(0, 3)) - powerMap(v+V2(0, 3))
      )
      val normal = gradient.cpy.nor()

      normalMap += (v -> gradient)

      if (power >= 0.99f && power <= 1.02f && probability(0.5f)) {
        roots ::= Root(v, normal.cpy().inverseX)
      }

      pixmap.drawPixel(x, y, Color.rgba8888(power, (normal.x+1f)/2f, (normal.y+1f)/2f))
    }

    (pixmap, normalMap, roots, roots.size)
  }

  def newIsland(min: Float) = Island(rV(gameSize*0.25f, gameSize*0.75f), rFloat(min, 18f))

  def pregenerate(numIslands: Int): Unit = {
    Future {
      val islands = generateIslands(numIslands)
      val dirtColors = newDirtColors
      val (dirtTexture, powerMap) = generateDirtTexture(islands.toList, dirtColors)
      val (normals, normalMap, roots, maxRoots) = generateNormals(powerMap)
      Some(Terrain(
        islands, dirtTexture, normals, powerMap, normalMap, roots, maxRoots,
        dirtColors, newGrassColors, newGrassLength
      ))
    }.onComplete {
      case Success(result) =>
        println("Terrain generated")
        Planet.terrain = result
        Planet.populateFromTerrain.set(true)
      case Failure(e) =>
        e.printStackTrace()
    }
  }

  def newDirtColors = {
    val start = rColor(0.15f, 0.8f)
    List(start, start.cpy().mul(0.5f).alpha(1f), start.cpy().mul(1.3f).alpha(1f))
  }
  def newGrassColors = {
    val start = rColor(C(0.25f, 0.25f, 0.15f), C(0.7f, 0.8f, 0.75f))
    List(start, start.cpy().mul(0.5f).alpha(1f), start.cpy().mul(1.3f).alpha(1f))
  }
  def newGrassLength = rFloat(8f, 38f)
}
