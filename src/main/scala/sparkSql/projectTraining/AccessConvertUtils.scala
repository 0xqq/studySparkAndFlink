package sparkSql.projectTraining

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}


/*
访问日志 转换工具
 */

object AccessConvertUtils {

    val struct = StructType{
      Array(
        StructField("url",StringType),
        StructField("cmsType",StringType),
        StructField("cmsId",LongType),
        StructField("traffic",LongType),
        StructField("ip",StringType),
        StructField("city",StringType),
        StructField("time",StringType),
        StructField("day",StringType)
      )
    }


  def parseLog(log:String) ={
    try{
      val words = log.split("\t")
      val url = words(1)
      val traffic = words(2).toLong
      val ip = words(3)
      val domain = "http://www.imooc.com/"
      val cms =url.substring(url.indexOf(domain)+domain.length)
      val cmsTypeId = cms.split("/")

      var cmsType = ""
      var cmsId = 0L
      if (cmsTypeId.length > 0){
        cmsType = cmsTypeId(0)
        cmsId = cmsTypeId(1).toLong
      }

      val city = ipUtils.getCity(ip)
      val time  =words(0)
      val day = time.substring(0,10).replaceAll("-","")

      Row(url,cmsType,cmsId,traffic,ip,city,time,day)

    }catch
      {
        case  e:Exception =>
          Row("","",0L,0L,"","","","")
      }



  }
}
