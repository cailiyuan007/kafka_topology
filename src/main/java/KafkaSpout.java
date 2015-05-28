import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaSpout implements IRichSpout {

    private SpoutOutputCollector collector;
    private ConsumerConnector consumer;
    private String topic;

    public KafkaSpout() {
    }

    public KafkaSpout(String topic) {
        this.topic = topic;
    }

    public void nextTuple() {
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    public void ack(Object msgId) {
    }

    public void activate() {

        consumer =kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
        Map<String,Integer> topickMap = new HashMap<String, Integer>();
        topickMap.put(topic, 1);

        System.out.println("*********Results********topic:"+topic);

        Map<String, List<KafkaStream<byte[],byte[]>>>  streamMap=consumer.createMessageStreams(topickMap);
        KafkaStream<byte[],byte[]>stream = streamMap.get(topic).get(0);
        ConsumerIterator<byte[],byte[]> it =stream.iterator();
        while(it.hasNext()){
            String line =new String(it.next().message());
            System.out.println("storm接收到来自kafka的消息------->" + line);

            String arr[] = line.split("\t",-1);
            String time = arr[0];
            String telephone = arr[1];

            collector.emit(new Values(time,telephone));
        }
    }

    private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        // 设置zookeeper的链接地址
        props.put("zookeeper.connect","master:2181,slave:2181");
        // 设置group id
        props.put("group.id", "1");
        // kafka的group 消费记录是保存在zookeeper上的, 但这个信息在zookeeper上不是实时更新的, 需要有个间隔时间更新
        props.put("auto.commit.interval.ms", "1000");
        props.put("zookeeper.session.timeout.ms","10000");
        return new ConsumerConfig(props);
    }

    public void close() {
    }

    public void deactivate() {
    }

    public void fail(Object msgId) {
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        declarer.declare(new Fields("time","telephone"));
    }

    public Map<String, Object> getComponentConfiguration() {
        System.out.println("getComponentConfiguration被调用");
        topic="idoall_topic";
        return null;
    }
}
