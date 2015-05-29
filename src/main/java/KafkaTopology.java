import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
/**
 * Created by CLY on 2015/5/28.
 */
public class KafkaTopology {
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new KafkaSpout(""), 1);
        builder.setBolt("bolt", new DataBolt()).shuffleGrouping("spout");

        Config config = new Config();
        config.setNumWorkers(4);
        config.setMaxSpoutPending(1000);

        StormSubmitter.submitTopology("my-flume-kafka-storm-topology-integration", config, builder.createTopology());
    }
}
