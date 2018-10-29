package sparkSql.projectTraining

import java.sql.{Connection, PreparedStatement}

import scala.collection.mutable.ListBuffer

object StatDAO {

  /**
    * 批量保存DayCityVideoAccessStat到数据库
    */
  def insertDayCityVideoAccessStatTopN(list:ListBuffer[dayCityVideoAcessStat]): Unit ={

    var connection:Connection = null
    var pstmt:PreparedStatement = null

    try{
      connection = MySQLUtils.getConnection()
      connection.setAutoCommit(false)
      val sql = "insert into day_video_city_access_topn_stat(day,cms_id,city,times,times_rank) values (?,?,?,?,?)"
      pstmt = connection.prepareStatement(sql)
      for(ele <- list){
        pstmt.setString(1,ele.day)
        pstmt.setLong(2,ele.cmsId)
        pstmt.setString(3,ele.city)
        pstmt.setLong(4,ele.times)
        pstmt.setLong(5,ele.timesRank)
        pstmt.addBatch()
      }
      pstmt.executeBatch() // 执行批量处理
      connection.commit() //手工提交

    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      MySQLUtils.release(connection, pstmt)
    }


  }



}
