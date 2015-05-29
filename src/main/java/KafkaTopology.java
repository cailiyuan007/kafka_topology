import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.spout.SchemeAsMultiScheme;

import storm.kafka.*;

/**
 * Created by CLY on 2015/5/28.
 */
public class KafkaTopology {
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException{
        // 1.指定broker地址
        String zks = "master:2181,slave:2181";
        BrokerHosts brokerHosts = new ZkHosts(zks);
        // 2.spout
        String topic = "idoall_topic";
        String zkRoot = "kafkastorm";
        String id = "appdata_monitor";

        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
        spoutConf.scheme = new SchemeAsMultScheme(new StringScheme());

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new KafkaSpout(spoutConf), 1);
        builder.setBolt("bolt", new DataBolt()).shuffleGrouping("spout");

        Config config = new Config();
        config.setNumWorkers(4);
        config.setMaxSpoutPending(1000);

        StormSubmitter.submitTopology("my-flume-kafka-storm-topology-integration", config, builder.createTopology());
    }
}
