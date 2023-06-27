package se.liu.ida.tdp024.account.util.kafka;

public interface KafkaHelper {
    void sendMessage(String topicName, String topicMsg);
}
