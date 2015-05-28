import java.util.HashMap;
import java.util.Map;
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
public class KafkaTopologytest {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new KafkaSpouttest(""), 1);
        builder.setBolt("bolt1", new Bolt1(), 2).shuffleGrouping("spout");
        builder.setBolt("bolt2", new Bolt2(), 2).fieldsGrouping("bolt1",new Fields("word"));

        Map conf = new HashMap();
        conf.put(Config.TOPOLOGY_WORKERS, 1);
        conf.put(Config.TOPOLOGY_DEBUG, true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("my-flume-kafka-storm-topology-integration", conf, builder.createTopology());

        Utils.sleep(1000*60*5); // local cluster test ...
        cluster.shutdown();
    }
}
