package scalaDemo

import akka.japi.Option.Some

//这一部分是apply的作用讲解

class applyDemo(var name:String,var age:Int){
}
object applyDemo {
  //这个方法是将方法注入
  //普通的类中需要写apply的
  def apply(name:String,age:Int): applyDemo  = new applyDemo(name,age)
  //Option是一个随意的值：在使用这个的时候，仅有两种情况，第一种是为空也就是None的情况下，第二种是就是Some,具体查看Option的代码案例
  //unapply是提取，所谓提取主要是进行值的模式匹配
  def unapply(applydemo: applyDemo): Option[(String,Int)] = {
    if (applydemo == null){
      None
    }else{
      Some(applydemo.name,applydemo.age)
    }
  }
}
object client{
  def main(args: Array[String]): Unit = {
    //这块便是apply方法的体现，不需要进行new 其实和case的表现是一样的
    val applydemo:applyDemo = applyDemo("li",12)

    //这块便是unapply的体现
    applydemo match {
      case applyDemo("li",age) => print(s"age: $age")
      case _ => print(None)
    }
  }
}