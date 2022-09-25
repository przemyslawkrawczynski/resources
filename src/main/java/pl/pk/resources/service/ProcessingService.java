package pl.pk.resources.service;


import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import pl.pk.resources.ExceptionUtils;
import pl.pk.resources.bussiness.ProcessingStatus;
import pl.pk.resources.bussiness.RegisterStatus;
import pl.pk.resources.bussiness.RegisteredRequest;
import pl.pk.resources.bussiness.Resource;
import pl.pk.resources.controller.RegisterController;
import pl.pk.resources.repository.RegisteredRequestRepository;
import pl.pk.resources.repository.ResourceRepository;

@Service
public class ProcessingService {

  private final Logger log = LoggerFactory.getLogger(RegisterController.class);
  private final ResourceRepository resourceRepository;
  private final RegisteredRequestRepository registeredRequestRepository;

  public ProcessingService(final ResourceRepository resourceRepository,
      final RegisteredRequestRepository registeredRequestRepository) {
    ExceptionUtils.notNull(resourceRepository, "SCIN_PK_20220925110842", log);
    ExceptionUtils.notNull(registeredRequestRepository, "SCIN_PK_20220925110850", log);

    this.resourceRepository = resourceRepository;
    this.registeredRequestRepository = registeredRequestRepository;
  }

  @KafkaListener(groupId = "processorGroups", topics = RegisterService.TOPIC, concurrency = "3")
  public void consume(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) UUID requestUUID, @Payload String url) {
    ExceptionUtils.notNull(requestUUID, "SCIN_PK_20220923175417", log);
    ExceptionUtils.notEmpty(url, "SCIN_PK_20220923175429", log);
    ExceptionUtils.canCreateURL(url, "SCIN_PK_20220923175447", log);

    process(requestUUID, url);
  }

  private void process(final UUID requestUUID, final String url) {

    final Optional<RegisteredRequest> registeredRequest = registeredRequestRepository.findByRequestUUID(requestUUID);
    if (!registeredRequest.isPresent()) {
      log.warn("Got request from unknown source...");
      throw new IllegalArgumentException("SCIN_PK_20220925133418");
    }

    Resource resource;
    try {
      final Connection connection = Jsoup.connect(url);
      final Response response = connection.execute();
      resource = Resource.registerCompleted(requestUUID, url, response.body());
    } catch (IOException ex) {
      resource = Resource.registerFailed(requestUUID, url);
    }

    resourceRepository.save(resource);
    markRegistrationRequestStatus(resource, registeredRequest.get());
  }

  private void markRegistrationRequestStatus(final Resource resource, final RegisteredRequest registeredRequest) {
    final RegisterStatus registerStatus =
        resource.getProcessingStatus() == ProcessingStatus.COMPLETED ? RegisterStatus.PROCESSED : RegisterStatus.FAIL;

    registeredRequest.setRegisterStatus(registerStatus);
    registeredRequestRepository.save(registeredRequest);
  }
}



