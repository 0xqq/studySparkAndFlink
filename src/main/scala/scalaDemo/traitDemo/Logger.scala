package scalaDemo.traitDemo

import java.io.PrintWriter

/*
当不存在具体实现及字段的特质，他最终生成字节码反编译是一个java的接口
当存在的话
 */


trait Logger {
  println("Logger")
  def log(msg:String)  //本身就是一个抽象方法
}

trait FileLogger extends Logger{
  println("FileLogger")
  val fileOutput = new PrintWriter("file.log")
  fileOutput.print("#")

  def log(msg:String): Unit ={
    fileOutput.println(msg)
    fileOutput.flush()
  }
}

object TraitDemo{
  def main(args: Array[String]): Unit = {
    //匿名类
    //不然发现特质是没有参数的构造器，但是他有构造器
    //不然这个代码是无法被执行的
    new FileLogger {}.log("trat demo")
  }
}