package pl.pk.resources.controller;

import java.util.Optional;
import java.util.UUID;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pk.resources.ExceptionUtils;
import pl.pk.resources.bussiness.ProcessingStatus;
import pl.pk.resources.bussiness.Resource;
import pl.pk.resources.repository.ResourceRepository;
import pl.pk.resources.service.ProcessingService;

@RestController
@RequestMapping("/resources")
public class ResourceController {

  private final Logger log = LoggerFactory.getLogger(ResourceController.class);

  private final ResourceRepository resourceRepository;
  private final ProcessingService processingService;

  private ResourceController(final ResourceRepository resourceRepository, final ProcessingService processingService) {
    ExceptionUtils.notNull(resourceRepository, "SCIN_PK_20220922121745", log);
    ExceptionUtils.notNull(processingService, "SCIN_PK_20220922121752", log);

    this.resourceRepository = resourceRepository;
    this.processingService = processingService;
  }

  @PostMapping("/register")
  private ProcessingStatus registerResourceToSave(final @RequestBody ResourceRequest request) {
    ExceptionUtils.notNull(request, "SCIN_PK_20220922094130", log);

    request.checkInputData();

    final ProcessingStatus result;

    // Oczywiscie do dyskusji i wymagan czy powinnismy jeszcze raz na proces wrzucic, czy wykonac inna akcje
    // Zakładam że nie rejestrujemy po raz drugi, stworzylbym endpoint w ktorym ponowiłbym procesowanie
    // Być może błąd że już zgłoszono, który frontend sobie jakos obsłuży?
    final Optional<Resource> resourceOptional = resourceRepository.findByRequestedURL(request.url);

    if (resourceOptional.isPresent()) {
      result = resourceOptional.get().getProcessingStatus();
    } else {
      final Resource resource = Resource.register(request.uuid, request.url);
      resourceRepository.save(resource);
      processingService.process(resource);
      result = resource.getProcessingStatus();
    }

    return result;
  }

  @ToString
  private static class ResourceRequest {

    private static final Logger log = LoggerFactory.getLogger(ResourceRequest.class);

    public UUID uuid;
    public String url;

    private void checkInputData() {
      ExceptionUtils.notNull(uuid, "SCIN_PK_20220922090140", log);
      ExceptionUtils.notEmpty(url, "SCIN_PK_20220922090150", log);
      ExceptionUtils.canCreateURL(url, "SCIN_PK_20220922091937", log);
    }
  }

}
