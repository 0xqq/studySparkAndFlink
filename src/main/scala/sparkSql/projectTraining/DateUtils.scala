package sparkSql.projectTraining

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import org.apache.commons.lang3.time.FastDateFormat


//日期时间解析
//FastDateFormat 使用这个类的一个原因在于SimpleDateFormat会出现线程资源问题，导致解析失败，原因在于，共享成员变量

object DateUtils {

  //10/Nov/2016:00:01:02 +0800
  val YYYYMMDDHHMM_TIME_FORMAT = FastDateFormat.getInstance("dd/MMM/yyyy:HH:mm:ss Z",Locale.ENGLISH)
  //日期的格式
  val TARGET_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")


  /**
    * 获取时间的类型 yyyy-MM-dd HH:mm:ss
    * @param time
    */

  def parse(time:String): String ={
    TARGET_FORMAT.format(new Date(getTime(time)))
  }

  /**
    *
    * @param time
    * @return
    */
  def getTime(time:String): Long ={
    try {
      YYYYMMDDHHMM_TIME_FORMAT.parse(time.substring(time.indexOf("[")+1,time.lastIndexOf("]"))).getTime
      }catch {
      case  e:Exception =>
        0L
    }
  }

  def main(args: Array[String]): Unit = {
    println(parse("[10/Nov/2016:00:01:02 +0800]"))
  }


}
