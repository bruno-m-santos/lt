package lt.serializers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MovieSerializer implements org.apache.kafka.common.serialization.Serializer {
    @Override
    public byte[] serialize(String topic, Object data) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }
}
