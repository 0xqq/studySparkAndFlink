package scalaDemo.traitDemo

object ConsoleLogger extends LoggerImp {
  def main(args: Array[String]): Unit = {
    ConsoleLogger.log("list")
  }
}
