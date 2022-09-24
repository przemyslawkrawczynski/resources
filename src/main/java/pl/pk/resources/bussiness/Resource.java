package pl.pk.resources.bussiness;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pk.resources.ExceptionUtils;

/**
 * @author pkrawczynski
 */
@Entity
@Table
@NoArgsConstructor
@Getter
public class Resource {

  private static final Logger log = LoggerFactory.getLogger(Resource.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Type(type="uuid-char")
  private UUID requestUUID;
  private String requestedURL;
  private LocalDateTime modificationDate;

  @Enumerated(EnumType.STRING)
  private ProcessingStatus processingStatus;

  @Lob
  private String resource;

  @Version
  private Integer version;

  private Resource(final UUID requestUUID, final String requestedURL, final LocalDateTime modificationDate,
      final ProcessingStatus processingStatus, final String resource) {
    this.requestUUID = requestUUID;
    this.requestedURL = requestedURL;
    this.modificationDate = modificationDate;
    this.processingStatus = processingStatus;
    this.resource = resource;
  }

  public static Resource registerCompleted(final UUID requestUUID, final String requestedURL, final String resource) {

    ExceptionUtils.notNull(requestUUID, "SCIN_PK_20220922075031", log);
    ExceptionUtils.notNull(requestedURL, "SCIN_PK_20220922084850", log);
    ExceptionUtils.notEmpty(resource, "SCIN_PK_20220923190319", log);

    return new Resource(requestUUID, requestedURL, LocalDateTime.now(), ProcessingStatus.COMPLETED, resource);
  }

  public static Resource registerFailed(final UUID requestUUID, final String requestedURL) {

    ExceptionUtils.notNull(requestUUID, "SCIN_PK_20220923190704", log);
    ExceptionUtils.notNull(requestedURL, "SCIN_PK_20220923190707", log);

    return new Resource(requestUUID, requestedURL, LocalDateTime.now(), ProcessingStatus.FAILED, null);
  }

}
