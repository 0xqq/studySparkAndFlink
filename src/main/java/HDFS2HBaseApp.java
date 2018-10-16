import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import java.io.IOException;

/**
 * 把HDFS上的数据导入到HBase上
 */
public class HDFS2HBaseApp {

    public static class MyMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put>{
        ImmutableBytesWritable rowkey = new ImmutableBytesWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] splits = value.toString().split(" ");
            rowkey.set(Bytes.toBytes(splits[0]));

            Put put = new Put(Bytes.toBytes(splits[0]));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("gender"), Bytes.toBytes(splits[1]));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes(splits[2]));

            context.write(rowkey, put);
        }
    }

    public static void main(String[] args) throws Exception{
        //创建Configuration
        Configuration configuration = new Configuration();
        configuration.set("hbase.zookeeper.quorum", "localhost");
        configuration.set("hbase.rootdir", "hdfs://localhost:8020/hbase");

        //创建job
        Job job = Job.getInstance(configuration, "HDFS2HBaseApp");
        //设置job的处理类
        job.setJarByClass(HDFS2HBaseApp.class);

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(Put.class);
        //第一个参数是用来做数据导入，hdfs位置，默认本地，传入参数可以
        FileInputFormat.addInputPath(job, new Path(args[0]));
        //第二个参数是用来做输出的hbase的表格
        TableMapReduceUtil.initTableReducerJob(
                args[1],        // output table
                null,    // reducer class
                job);
        job.setNumReduceTasks(1);   // at least one, adjust as required

        boolean b = job.waitForCompletion(true);
        if (!b) {
            throw new IOException("error with job!");
        }
    }

}
