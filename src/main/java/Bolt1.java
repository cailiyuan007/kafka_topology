import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
/**
 * Created by CLY on 2015/5/28.
 */
public class Bolt1 extends BaseBasicBolt{
    public void execute(Tuple input, BasicOutputCollector collector) {
        try {
            String msg = input.getString(0);
            int id = input.getInteger(1);
            String time = input.getString(2);
            msg = msg+"bolt1";
            System.out.println("对消息加工第1次-------[arg0]:"+ msg +"---[arg1]:"+id+"---[arg2]:"+time+"------->"+msg);
            if (msg != null) {
                collector.emit(new Values(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}
