package lt.Serializers;

import lt.avro.Movie;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MovieDeserializer implements org.apache.kafka.common.serialization.Deserializer {
    @Override
    public Object deserialize(String topic, byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = null;
        Object obj = new Object();
        try {
            is = new ObjectInputStream(in);
            Movie movie = null;
            obj = is.readObject();
            if (obj instanceof Movie) {
                System.out.println("Tipo Movie -> -> ->" + obj.toString());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
