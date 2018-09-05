package state

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}

object ValueStateKeyedStreamValueStateOption extends App{
  val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

  //用户函数获取in中的当前值，并且必须返回将用于更新状态的更新值，这个返回回来的是更新状态的更新值
  val mapRes = env.fromCollection(List(
    ("c", 4),
    ("c", 3),
    ("c", 1),
    ("c", 5),
    ("a", 242),
    ("a", 53),
    ("a", 35),
    ("d", 4),
    ("d", 3),
    ("d", 3),
    ("b", 4),
    ("b", 3),
    ("b", 3)
  )).keyBy(_._1)
    .mapWithState(
    (in: (String, Int), count: Option[Int]) =>
    count match {
      case Some(c) => ( (in._1, c),  Some(c + in._2))
      case None => ( (in._1, 0), Some(in._2) )
    }
  )
  mapRes.print()

  env.execute("ValueStateKeyedStreamValueStateOption")

}
