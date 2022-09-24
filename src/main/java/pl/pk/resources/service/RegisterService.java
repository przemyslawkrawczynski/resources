package pl.pk.resources.service;

import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.pk.resources.ExceptionUtils;

@Service
public class RegisterService {

  // Do Configuration registry można
  static final String TOPIC = "resources";
  private static final Logger log = LoggerFactory.getLogger(RegisterService.class);
  private final KafkaTemplate<UUID, String> kafkaTemplate;

  public RegisterService(final KafkaTemplate<UUID, String> kafkaTemplate) {
    ExceptionUtils.notNull(kafkaTemplate, "SCIN_PK_20220923153850", log);
    this.kafkaTemplate = kafkaTemplate;
  }

  @Transactional
  public void registerResource(final UUID requestUUId, final String url) {
    ExceptionUtils.notNull(requestUUId, "SCIN_PK_20220923154045", log);
    ExceptionUtils.notEmpty(url, "SCIN_PK_20220923154059", log);
    ExceptionUtils.canCreateURL(url, "SCIN_PK_20220923154131", log);

    kafkaTemplate.send(TOPIC, requestUUId, url);
  }
}
