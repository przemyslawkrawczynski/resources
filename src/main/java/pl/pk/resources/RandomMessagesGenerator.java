package pl.pk.resources;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import pl.pk.resources.configuration.KafkaConfiguration;
import pl.pk.resources.service.RegisterService;

public class RandomMessagesGenerator {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    final KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
    final KafkaProducer<UUID, String> producer = new KafkaProducer<>(kafkaConfiguration.producerConfigs());

    for (int i = 0; i < 5; i++) {
      UUID uuid = UUID.randomUUID();
      String url = "http://google.pl/search?q=" + i;
      ProducerRecord<UUID, String> record = new ProducerRecord<>(RegisterService.TOPIC, uuid, url);
      final RecordMetadata recordData = producer.send(record).get();
      System.out.println("Zarejestrowano - " + i);
    }
  }
}
