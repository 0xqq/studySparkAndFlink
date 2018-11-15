package scalaDemo.traitDemo

trait LoggerImp {
  def log(msg:String): Unit ={
    print(msg)
  }
}
