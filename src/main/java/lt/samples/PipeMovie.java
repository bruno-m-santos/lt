package lt.samples;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class PipeMovie {

    public static void main(String args[]){

        Properties prop = new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "LT-streams");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, byte[]> source = builder.stream("streams-lt-input");

        source
//            .flatMapValues(value -> {
//                ByteArrayInputStream in = new ByteArrayInputStream(value.clone());
//                ObjectInputStream is = null;
//                try {
//                    is = new ObjectInputStream(in);
//                    return Arrays.asList(is.readObject());
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                return Arrays.asList(value);
//            })
//            .groupBy((k,v) -> v)
//            .count()
//            .toStream()
            .to("streams-lt-output");

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology,prop);
        final CountDownLatch latch = new CountDownLatch(1);

        streams.start();

    }
}
