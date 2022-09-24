package pl.pk.resources;

import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import pl.pk.resources.configuration.KafkaConfiguration;

public class RandomMessagesGenerator {

  public static void main(String[] args) {

    final KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
    final KafkaProducer<UUID, String> producer = new KafkaProducer<>(kafkaConfiguration.producerConfigs());

    for (int i = 0; i < 100; i++) {
      UUID uuid = UUID.randomUUID();
      String url = "http://google.pl/search?q=" + i;
      ProducerRecord<UUID, String> record = new ProducerRecord<>("resources", uuid, url);
      producer.send(record);
    }

  }
}
