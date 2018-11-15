package scalaDemo

object closureAndHelve {
  def main(args: Array[String]): Unit = {
    //闭包是一个函数，返回值依赖于声明在函数外部的一个或者多个变量
    //闭包通常来讲，可以简单的认为是可以访问一个函数里面的局部变量里面的另一个函数
    def mulby(factor:Double) = (x:Double) => x * factor
    var a = mulby(3)
    var b = mulby(0.5)
//    println(a(2) + b(9))
//    print(mulby(2)(9))
    //柯里化
    def add(a:Int) = (b:Int) => a + b
//    print(add(1)(2))
  }

}
