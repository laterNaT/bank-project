package se.liu.ida.tdp024.account.util.kafka;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaHelperImpl implements KafkaHelper {
    private KafkaProducer<String, String> producer;

    public static final String TRANSACTION_TOPIC = "transaction-topic";
    public static final String REST_API_TOPIC = "restapi-topic";

    public KafkaHelperImpl() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<String, String>(props);
    }

    @Override
    public void sendMessage(String topicName, String topicMsg) {
        Date date = new Date();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd H:m:s").format(date);
        this.producer.send(new ProducerRecord<String,String>(topicName, formattedDate + " " + topicMsg));
    }
}
