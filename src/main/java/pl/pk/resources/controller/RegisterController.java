package pl.pk.resources.controller;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pk.resources.ExceptionUtils;
import pl.pk.resources.bussiness.RegisterStatus;
import pl.pk.resources.bussiness.RegisteredRequest;
import pl.pk.resources.repository.ResourceRepository;
import pl.pk.resources.service.ProcessingService;
import pl.pk.resources.service.RegisterService;

@RestController
@RequestMapping("/register")
public class RegisterController {

  private final Logger log = LoggerFactory.getLogger(RegisterController.class);

  private final ResourceRepository resourceRepository;
  private final ProcessingService processingService;
  private final RegisterService registerService;

  private RegisterController(final ResourceRepository resourceRepository, final ProcessingService processingService,
      final RegisterService registerService) {
    ExceptionUtils.notNull(resourceRepository, "SCIN_PK_20220922121745", log);
    ExceptionUtils.notNull(processingService, "SCIN_PK_20220922121752", log);
    ExceptionUtils.notNull(processingService, "SCIN_PK_20220923154440", log);

    this.resourceRepository = resourceRepository;
    this.processingService = processingService;
    this.registerService = registerService;
  }

  @PostMapping("/webpage")
  private ResourceRequestResponse registerResourceToSave(final @RequestBody ResourceRequest request) {
    ExceptionUtils.notNull(request, "SCIN_PK_20220922094130", log);
    request.checkInputData();

    final ResourceRequestResponse result;

    final RegisteredRequest registeredRequest = registerService.registerWebPageResourcesToSave(request.url);
    result = ResourceRequestResponse.createResponse(registeredRequest);

    return result;
  }

  // to testing
  @GetMapping("/{number}")
  private boolean registerMultiple(@PathVariable int number) {
    for (int i = 0; i < number; i++) {
      String url = "http://google.pl/search?q=" + i;
      registerService.registerWebPageResourcesToSave(url);
    }

    return true;
  }

  @ToString
  private static class ResourceRequest {

    private static final Logger log = LoggerFactory.getLogger(ResourceRequest.class);
    public String url;

    private void checkInputData() {
      ExceptionUtils.notEmpty(url, "SCIN_PK_20220922090150", log);
      ExceptionUtils.canCreateURL(url, "SCIN_PK_20220922091937", log);
    }
  }

  @ToString
  @AllArgsConstructor
  private static class ResourceRequestResponse {

    public UUID uuid;
    public String url;
    public LocalDateTime firstRegistrationTime;
    public RegisterStatus registerStatus;

    private static ResourceRequestResponse createResponse(final RegisteredRequest request) {
      return new ResourceRequestResponse(request.getRequestUUID(), request.getRequestedURL(),
          request.getFirstRegistrationDate(), request.getRegisterStatus());
    }
  }
}
