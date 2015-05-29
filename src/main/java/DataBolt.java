import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by CLY on 2015/5/28.
 */
public class DataBolt extends BaseBasicBolt{
    public Configuration config;
    public HTable table;
    public void prepare(Map stormConf, TopologyContext context) {
        config = HBaseConfiguration.create();
        try {
            table = new HTable(config, Bytes.toBytes("appdata_monitor"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(Tuple input, BasicOutputCollector collector) {
        String rowkey = TransferTime(input.getString(0));
        String telephone = input.getString(1);

        Put put = new Put(Bytes.toBytes(rowkey));

        long count = 0L;
        long empty_count = 0L;
        try {
            count = table.incrementColumnValue(Bytes.toBytes(rowkey),
                    Bytes.toBytes("cf"), Bytes.toBytes("total"), 1L);
            put.add(Bytes.toBytes("cf"), Bytes.toBytes("total_str"),
                    Bytes.toBytes(Long.toString(count)));
            if (telephone.equals("")) {
                empty_count = table.incrementColumnValue(
                        Bytes.toBytes(rowkey), Bytes.toBytes("cf"),
                        Bytes.toBytes("empty"), 1L);
                put.add(Bytes.toBytes("cf"), Bytes.toBytes("empty_str"),
                        Bytes.toBytes(Long.toString(empty_count)));
            }
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        try {
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("time","telephone"));
    }

    public String TransferTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/ShangHai"));
        String sd = sdf.format(new Date(Long.parseLong(time)));
        return sd;
    }
}
