package lt.samples;

import lt.Serializers.MovieDeserializer;
import lt.Serializers.MovieSerializer;
import lt.avro.Movie;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Properties;

public class PipeMovie {

    public static void main(String args[]){

        Properties prop = new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "LT-streams");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        Serde<String> stringSerdes = Serdes.String();
        Serde<byte[]> byteArraySerdes = Serdes.ByteArray();

        KStream<String, Movie> source = builder.stream("streams-lt-input", Consumed.with(stringSerdes,byteArraySerdes))
                .map((key,value) -> {
                    MovieDeserializer v = new MovieDeserializer();
                    Object movieObj = v.deserialize("streams-lt-input", value);
                    return KeyValue.pair(key,  Movie.newBuilder().setMovieId(((Movie) movieObj).getMovieId()).setReleaseYear(((Movie) movieObj).getReleaseYear()).setTitle(((Movie) movieObj).getTitle()).build());
                });

        KStream sourceStream = source.flatMapValues(value -> Arrays.asList(value.getTitle().toString()));

        KGroupedStream grouped = sourceStream. groupBy((k, v) -> v);

        KStream stream = grouped
                .count()
                .toStream();

        stream.to("streams-lt-output", Produced.with(Serdes.String(), Serdes.Long()));

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology,prop);

        streams.start();

    }
}
