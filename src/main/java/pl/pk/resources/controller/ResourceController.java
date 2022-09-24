package pl.pk.resources.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pk.resources.ExceptionUtils;
import pl.pk.resources.bussiness.ProcessingStatus;
import pl.pk.resources.bussiness.RegisterStatus;
import pl.pk.resources.bussiness.Resource;
import pl.pk.resources.repository.ResourceRepository;
import pl.pk.resources.service.ProcessingService;
import pl.pk.resources.service.RegisterService;

@RestController
@RequestMapping("/resources")
public class ResourceController {

  private final Logger log = LoggerFactory.getLogger(ResourceController.class);

  private final ResourceRepository resourceRepository;
  private final ProcessingService processingService;
  private final RegisterService registerService;

  private ResourceController(final ResourceRepository resourceRepository, final ProcessingService processingService,
      final RegisterService registerService) {
    ExceptionUtils.notNull(resourceRepository, "SCIN_PK_20220922121745", log);
    ExceptionUtils.notNull(processingService, "SCIN_PK_20220922121752", log);
    ExceptionUtils.notNull(processingService, "SCIN_PK_20220923154440", log);

    this.resourceRepository = resourceRepository;
    this.processingService = processingService;
    this.registerService = registerService;
  }

  @PostMapping("/register")
  private ResourceRequestResponse registerResourceToSave(final @RequestBody ResourceRequest request) {
    ExceptionUtils.notNull(request, "SCIN_PK_20220922094130", log);

    request.checkInputData();

    final ResourceRequestResponse result;

    // Oczywiscie do dyskusji i wymagan czy powinnismy jeszcze raz na proces wrzucic, czy wykonac inna akcje
    // Zakładam że nie rejestrujemy po raz drugi, stworzylbym endpoint w ktorym ponowiłbym procesowanie
    // Być może błąd że już zgłoszono, który frontend sobie jakos obsłuży?
    final Optional<Resource> resourceOptional = resourceRepository.findByRequestUUID(request.uuid);

    if (resourceOptional.isPresent() && (ProcessingStatus.COMPLETED == resourceOptional.get().getProcessingStatus())) {
      result = ResourceRequestResponse.createResponse(request, RegisterStatus.ALREADY_EXIST);
    } else {
      registerService.registerResource(request.uuid, request.url);
      result = ResourceRequestResponse.createResponse(request, RegisterStatus.REGISTERED);
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

  @ToString
  @AllArgsConstructor
  private static class ResourceRequestResponse {

    public UUID uuid;
    public String url;
    public LocalDateTime registered;
    public RegisterStatus registerStatus;

    private static ResourceRequestResponse createResponse(final ResourceRequest request,
        RegisterStatus registerStatus) {
      return new ResourceRequestResponse(request.uuid, request.url, LocalDateTime.now(), registerStatus);
    }
  }

}
