package redisCon

import redis.clients.jedis.Jedis

object redisCli {
  def main(args: Array[String]): Unit = {
    var jr: Jedis = null
    try {
      jr = new Jedis("127.0.0.1",6379)
      println(jr.ping())
      System.out.println(jr.isConnected && jr.ping.equals("PONG"))
      val key = "h*"
      val res = jr.keys(key)
      print(res)


    }catch {
      case t =>
        t.printStackTrace()
    }
  }
}
