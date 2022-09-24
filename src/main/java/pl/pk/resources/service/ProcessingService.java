package pl.pk.resources.service;


import java.io.IOException;
import java.util.UUID;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import pl.pk.resources.ExceptionUtils;
import pl.pk.resources.bussiness.Resource;
import pl.pk.resources.controller.ResourceController;
import pl.pk.resources.repository.ResourceRepository;

@Service
public class ProcessingService {

  private final Logger log = LoggerFactory.getLogger(ResourceController.class);
  private final ResourceRepository resourceRepository;

  public ProcessingService(final ResourceRepository resourceRepository) {
    ExceptionUtils.notNull(resourceRepository, "SCIN_PK_20220922121928", log);

    this.resourceRepository = resourceRepository;
  }

  @KafkaListener(groupId = "processorGroups", topics = RegisterService.TOPIC, concurrency = "3")
  public void consume(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) UUID requestUUID, @Payload String url) {
    ExceptionUtils.notNull(requestUUID, "SCIN_PK_20220923175417", log);
    ExceptionUtils.notEmpty(url, "SCIN_PK_20220923175429", log);
    ExceptionUtils.canCreateURL(url, "SCIN_PK_20220923175447", log);

    process(requestUUID, url);
  }

  private void process(final UUID requestUUID, final String url) {
    Resource resource;

    try {
      final Connection connection = Jsoup.connect(url);
      final Response response = connection.execute();
      resource = Resource.registerCompleted(requestUUID, url, response.body());

    } catch (IOException ex) {
      resource = Resource.registerFailed(requestUUID, url);
    }

    resourceRepository.save(resource);
  }
}



