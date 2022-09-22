package pl.pk.resources.service;


import java.io.IOException;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pk.resources.ExceptionUtils;
import pl.pk.resources.bussiness.ProcessingStatus;
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

  @Transactional(TxType.REQUIRES_NEW)
  public void process(final Resource resource) {

    ExceptionUtils.notNull(resource, "SCIN_PK_20220922120059", log);
    ExceptionUtils.notNull(resource.getRequestedURL(), "SCIN_PK_20220922120123", log);

    try {
      log.info("Próba nawiązania połączenia - {}", resource.getRequestedURL());
      final Connection connection = Jsoup.connect(resource.getRequestedURL());
      final Response response = connection.execute();

      // Czy jak będzie puste body to dopuszczamy?
      // Można pokusić się o zapis HttpStatus codu?
      if (response != null && response.statusCode() == HttpStatus.OK.value()) {
        log.info("Nawiązano połączenie.. zapis danych");
        // TO-DO timeProvider
        resource.setCompletitionDate(LocalDateTime.now());
        resource.setProcessingStatus(ProcessingStatus.COMPLETED);
        resource.setResource(response.body());
      } else {
        resource.setProcessingStatus(ProcessingStatus.FAILED);
      }
    } catch (IllegalArgumentException | IOException ex) {
      log.warn("Wystąpił problem z linkiem...");
      // niezgodne z FF - do dyskusji :)
      resource.setProcessingStatus(ProcessingStatus.FAILED);
    }
  }
}



