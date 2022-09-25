package pl.pk.resources.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import pl.pk.resources.ExceptionUtils;
import pl.pk.resources.bussiness.RegisterStatus;
import pl.pk.resources.bussiness.RegisteredRequest;
import pl.pk.resources.repository.RegisteredRequestRepository;

@Service
public class RegisterService {

  public static final String TOPIC = "resources";
  private static final Logger log = LoggerFactory.getLogger(RegisterService.class);
  private final KafkaTemplate<UUID, String> kafkaTemplate;
  private final RegisteredRequestRepository registeredRequestRepository;

  public RegisterService(final KafkaTemplate<UUID, String> kafkaTemplate,
      final RegisteredRequestRepository registeredRequestRepository) {
    ExceptionUtils.notNull(kafkaTemplate, "SCIN_PK_20220923153850", log);
    ExceptionUtils.notNull(registeredRequestRepository, "SCIN_PK_20220924184916", log);

    this.kafkaTemplate = kafkaTemplate;
    this.registeredRequestRepository = registeredRequestRepository;
  }

  // Do dyskusji czy chcemy ponownie rejestrować. Ja założyłem że nie i zwróce uuid do późniejszych sprawdzeń co z plikiem
  public RegisteredRequest registerWebPageResourcesToSave(final String url) {
    ExceptionUtils.notEmpty(url, "SCIN_PK_20220923154059", log);
    ExceptionUtils.canCreateURL(url, "SCIN_PK_20220923154131", log);

    final Optional<RegisteredRequest> registeredRequst = registeredRequestRepository.findByRequestedURL(url);
    if (registeredRequst.isPresent() && RegisterStatus.FAIL != registeredRequst.get().getRegisterStatus()) {
      return registeredRequst.get();
    }

    final UUID requestResourceUUID = generateRegisterUUID();
    final RegisteredRequest request = registeredRequestRepository.save(
        RegisteredRequest.register(requestResourceUUID, url, LocalDateTime.now()));

    startProcessingResource(requestResourceUUID, url);

    return request;
  }

  private void startProcessingResource(final UUID requestUUID, final String requestUrl) {
    kafkaTemplate.send(TOPIC, requestUUID, requestUrl);
  }

  private UUID generateRegisterUUID() {
    final UUID result = UUID.randomUUID();
    return result;
  }
}
