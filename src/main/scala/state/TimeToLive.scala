package state
import org.apache.flink.api.common.state.{StateTtlConfig, ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.time.Time


object TimeToLive {
 val ttlConfig =  StateTtlConfig.
   newBuilder(Time.seconds(1)). // 这个时间的参数是必须给定的，制定了一个时间的ttl
   setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite).//仅限创建和写入权限
   setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired). //从来没有返回用户到期值
   build()

  val stateDescriptor = new ValueStateDescriptor[String]("text state",classOf[String])

  stateDescriptor.enableTimeToLive(ttlConfig)



}
