package lt.samples;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lt.avro.Movie;
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
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class);

        KafkaProducer<String, byte[]> myProducer= new KafkaProducer<String, byte[]>(properties);

        try {

            for(int i=1;i<10;i++){

                Movie movie = new Movie();
                movie.setMovieId(Long.valueOf(i));
                movie.setReleaseYear(Integer.valueOf(i));
                movie.setTitle("ddddddd");

                Name name = new Name();
                name.setName(String.valueOf(i));

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(movie);
                oos.flush();
                byte [] data = bos.toByteArray();

                myProducer.send(new  ProducerRecord<String, byte[]>("streams-lt-input", KafkaProducer.class.getName() , data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            myProducer.close();
        }
    }

}
