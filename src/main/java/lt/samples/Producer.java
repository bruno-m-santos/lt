package lt.samples;

import lt.serializers.MovieSerializer;
import lt.avro.Movie;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class Producer  {

    public static void main(String[] args) {

        Properties properties=new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "LT-streams");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        KafkaProducer<String, byte[]> myProducer = new KafkaProducer<String, byte[]>(properties);

        for(int i=1;i<10;i++){

            Movie movie = new Movie().newBuilder().setMovieId(i).setReleaseYear(1998).setTitle("Title" + String.valueOf(i)).build();
            MovieSerializer v = new MovieSerializer();

            byte[] serdes2 = v.serialize("streams-lt-input", movie);
            myProducer.send(new  ProducerRecord<String, byte[]>("streams-lt-input", KafkaProducer.class.getName() , serdes2));
        }

    }

}
