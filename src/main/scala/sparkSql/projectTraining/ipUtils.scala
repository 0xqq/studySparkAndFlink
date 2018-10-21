package sparkSql.projectTraining

import com.ggstar.util.ip.IpHelper

object ipUtils {
  def getCity(ip:String): String ={
    IpHelper.findRegionByIp(ip)
  }

  def main(args: Array[String]): Unit = {
    println(getCity("218.241.231.82"))
  }

}
