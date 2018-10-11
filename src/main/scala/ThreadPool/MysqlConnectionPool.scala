package ThreadPool
import java.sql.{Connection, DriverManager}

import org.apache.commons.pool2.impl.{DefaultPooledObject, GenericObjectPool}
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}




object MysqlConnectionPool extends Serializable {

  private  val pool = new GenericObjectPool[Connection](new MysqlConnectionFactory("jdbc:mysql://localhost:3306/StructruedStreaming", "root", "Server2008!", "com.mysql.jdbc.Driver"))
//  private  val pool = new GenericObjectPool[Connection](new MysqlConnectionFactory(url,userName,password,"com.mysql.jdbc.Driver"))


  def getConnection(): Connection ={
    pool.borrowObject()
  }

  def returnConnection(conn: Connection): Unit ={
    pool.returnObject(conn)
  }
}


@transient
class MysqlConnectionFactory(url: String, userName: String, password: String, className: String) extends BasePooledObjectFactory[Connection] {
  override def create(): Connection = {
    Class.forName(className)
    DriverManager.getConnection(url, userName, password)
  }
  override def wrap(conn: Connection): PooledObject[Connection] = new DefaultPooledObject[Connection](conn)

  override def validateObject(pObj: PooledObject[Connection]) = !pObj.getObject.isClosed

  override def destroyObject(pObj: PooledObject[Connection]) = pObj.getObject.close()

}
