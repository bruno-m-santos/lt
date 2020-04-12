package lt.samples;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lt.Serializers.MovieSerializer;
import lt.avro.Movie;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;


class Name extends AbstractKafkaAvroSerializer{
     String name;

     public void setName(String name){
          this.name = name;
     }
     public String getName()
     {
         return this.name;
     }
}

public class Producer  {

    public static void main(String[] args) {

        Properties properties=new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "LT-streams");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
               ByteArraySerializer.class);

        KafkaProducer<String, byte[]> myProducer = new KafkaProducer<String, byte[]>(properties);

        for(int i=1;i<10;i++){

            Movie movie = new Movie().newBuilder().setMovieId(i).setReleaseYear(1998).setTitle("Title" + String.valueOf(i)).build();
            MovieSerializer v = new MovieSerializer();

//            GenericData.Record record = new GenericData.Record(movie.getClassSchema());
//            record.put("movie_id", movie.getMovieId());
//            record.put("release_year", movie.getReleaseYear());
//            record.put("title", movie.getTitle());
//            byte[] serdes1 = v.serialize("streams-lt-input", record.);
//            myProducer.send(new  ProducerRecord<String, byte[]>("streams-lt-input", KafkaProducer.class.getName() , record.toString().getBytes()));


            byte[] serdes2 = v.serialize("streams-lt-input", movie);
            myProducer.send(new  ProducerRecord<String, byte[]>("streams-lt-input", KafkaProducer.class.getName() , serdes2));
        }

    }

}
