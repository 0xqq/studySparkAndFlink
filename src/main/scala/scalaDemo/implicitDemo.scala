package scalaDemo

import scala.language.implicitConversions

object implicitDemo {
  implicit def int2Fraction (x:Float):Int = x.toInt
  def main(args: Array[String]): Unit = {

    val x:Float = 2
    println(x)
  }
  def Fractiona(i: Float, i1: Float): Float ={
    val res = i1 / i
    res
  }

}
