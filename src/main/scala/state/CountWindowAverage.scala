package state

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeHint, TypeInformation}
import org.apache.flink.configuration.Configuration
import org.apache.flink.queryablestate.client.QueryableStateClient
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.apache.flink.util.Collector


//这个接口的稳定性有待商榷

class CountWindowAverage extends RichFlatMapFunction[(Long, Long), (Long, Long)] {
  private  var sum: ValueState[(Long, Long)] = _
  override def flatMap(in: (Long, Long), out: Collector[(Long, Long)]): Unit = {
    val tmpCurrentSum:(Long, Long) = sum.value

    val currentSum:(Long, Long) = if (tmpCurrentSum != null) {
      (tmpCurrentSum._1+1,in._2)
    } else {
      (0L, 0L)
    }

    sum.update(currentSum)

    if (currentSum._1 >= 2){
      out.collect((in._1,currentSum._2/currentSum._1))
      sum.clear()
    }

  }
  override def open(parameters: Configuration) = {
   val descriptor: ValueStateDescriptor[(Long, Long)] =  new ValueStateDescriptor[(Long, Long)]("average", createTypeInformation[(Long, Long)])
    descriptor.setQueryable("query-name")
    sum = getRuntimeContext.getState(descriptor)
  }
}

object CountWindowAverage extends App {
 val queryableStateClient =  new QueryableStateClient("",9090)

 val descriptor : ValueStateDescriptor[(Long, Long)]  = new ValueStateDescriptor[(Long, Long)]("average", TypeInformation.of(new TypeHint[(Long,Long)]{}))



}
