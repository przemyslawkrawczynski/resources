package pl.pk.resources.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pk.resources.bussiness.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

  Optional<Resource> findByRequestedURL(final String requestedUrl);
}
