package redisCon


//在使用jedis的时候，api已经实现就业commons-pool2的jedis的线程池

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig, Pipeline}

object redisCli {
  def main(args: Array[String]): Unit = {
    var jrconf: JedisPoolConfig = null
    var jrpool: JedisPool = null
    var jr: Jedis = null
    try {
      jrconf = new JedisPoolConfig()
      jrconf.setMaxTotal(10)
      jrpool = new JedisPool(jrconf,"localhost",6379)

      jr = jrpool.getResource()
      println(jr.ping())

    }catch {
      case t =>
        t.printStackTrace()
    } finally {
      //还回pool中
      if(jr != null){
        jr.close();
      }
    }
    jrpool.close()
  }
}
