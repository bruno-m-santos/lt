package lt.samples;

import lt.avro.Movie;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Pipe {

    public static void main(String args[]){

        Properties prop = new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "LT-streams");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //computational logic
        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> source = builder.stream("streams-lt-input");
        KStream<String, Movie> source2 = builder.stream("streams-lt-input");

        KeyValueMapper keyMapperValues = new KeyValueMapper() {
            @Override
            public Object apply(Object key, Object value) {
                return value;
            }
        };

        /*TO DO fazer o stream genérico para qualquer tipo de entrada*/
        source
                //.filter((key,value) -> value.length() < 3).
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split(",")))
                .flatMapValues(value -> Arrays.asList(value + value.length()))
                .flatMapValues((key,value) -> Arrays.asList(value.toLowerCase() + "teste444....." + key))
                //.groupBy(keyMapperValues)
                .groupBy((k,v) -> v)
                .count()
                .toStream()
                .to("streams-lt-output", Produced.with(Serdes.String(), Serdes.Long()));

         // --stateless transformation
        //source.mapValues((k,v) -> v); devolve um valor
        //source.map((k,v) -> KeyValue.pair(k,v)); devolve um record
        //source.flatMap((k,v) -> Arrays.asList(KeyValue.pair(k,v))); devolve uma lista de record
        //source.flatMapValues((k,v) -> Arrays.asList(v)); devolve um valor

        final Topology topology = builder.build();

        /*
        Sub-topologies:
           Sub-topology: 0
           Source: KSTREAM-SOURCE-0000000000(topics: streams-plaintext-input) --> KSTREAM-SINK-0000000001
           Sink: KSTREAM-SINK-0000000001(topic: streams-pipe-output) <-- KSTREAM-SOURCE-0000000000
        Global Stores:
           none
        */
       // System.out.println(topology.describe());
       // builder.table("treams-lt-input").mapValues((k,v) -> v).print(Printed.toSysOut());

        final KafkaStreams streams = new KafkaStreams(topology,prop);
        final CountDownLatch latch = new CountDownLatch(1);

        streams.start();
        // attach shutdown handler to catch control-c
//        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
//            @Override
//            public void run() {
//                streams.close();
//                latch.countDown();
//            }
//        });
//
//        try {
//            streams.start();
//            latch.await();
//        } catch (Throwable e) {
//            System.exit(1);
//        }
//        System.exit(0);
    }
}
