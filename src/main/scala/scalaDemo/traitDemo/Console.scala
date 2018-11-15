package scalaDemo.traitDemo



class Console extends Logger with Serializable{
  override def log(msg: String): Unit = {
    println("msg："+msg)
  }
}
object Console{
  def apply: Console = new Console()
  def main(args: Array[String]): Unit = {
    apply.log("你好")
    val log =new  Console()
  }
}
