import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by CLY on 2015/5/28.
 */
public class KafkaTopology {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new KafkaSpout(""), 1);
        builder.setBolt("bolt", new DataBolt()).shuffleGrouping("spout");

        Map conf = new HashMap();
        conf.put(Config.TOPOLOGY_WORKERS, 1);
        conf.put(Config.TOPOLOGY_DEBUG, true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("my-flume-kafka-storm-topology-integration", conf, builder.createTopology());

        Utils.sleep(1000*60*5); // local cluster test ...
        cluster.shutdown();
    }
}
