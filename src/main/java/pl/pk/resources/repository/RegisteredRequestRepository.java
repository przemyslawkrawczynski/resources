package pl.pk.resources.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pk.resources.bussiness.RegisteredRequest;

public interface RegisteredRequestRepository extends JpaRepository<RegisteredRequest, Long> {

  Optional<RegisteredRequest> findByRequestedURL(final String requestedUrl);
  Optional<RegisteredRequest> findByRequestUUID(final UUID requestUUID);
}
