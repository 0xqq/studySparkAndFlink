package scalaDemo.traitDemo

import java.io.PrintWriter

trait Logger_lazy {
  def log(msg:String):Unit
}
//这个就像抽象方法
trait FileLogger1 extends Logger_lazy {

  //增加了抽象成员变量
  val fileName: String
  //将抽象成员变量作为PrintWriter参数
  lazy val fileOutput = new PrintWriter(fileName: String)
  //因为是lazy的方式，所以在不使用的情况下是不会进行调用，直到进行了调用
//  fileOutput.println("#")

  def log(msg: String): Unit = {
    fileOutput.print(msg)
    fileOutput.flush()
  }
}

class Person
class Student extends Person with FileLogger1{
  val fileName :String = "file.log"
}
object TraitDemo2{
  def main(args: Array[String]): Unit = {
//    new Student().log("trait")//这种方式在创建的时候，初始化并没有将真正的值传递到PrintWriter中
    //以下的方式是比较难以看懂的
    /*
    val s=new {
      //提前定义
      override val fileName="file.log"
    } with Student
    s.log("trait demo")
*/

    val  s= new Student
    s.log("trait")
  }
}