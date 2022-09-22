package pl.pk.resources.bussiness;

import java.sql.Clob;
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
import lombok.Setter;
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
@Setter
public class Resource {

  private static final Logger log = LoggerFactory.getLogger(Resource.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private UUID requestUUID;
  private LocalDateTime requestDate;
  private String requestedURL;
  private LocalDateTime completitionDate;

  @Enumerated(EnumType.STRING)
  private ProcessingStatus processingStatus;

  @Lob
  private String resource;

  @Version
  private Integer version;

  private Resource(final UUID requestUUID, final LocalDateTime requestDate, final String requestedURL,
      final ProcessingStatus processingStatus) {

    this.requestUUID = requestUUID;
    this.requestDate = requestDate;
    this.requestedURL = requestedURL;
    this.processingStatus = processingStatus;
  }

  public static Resource register(final UUID requestUUID, final String requestedURL) {

    ExceptionUtils.notNull(requestUUID, "SCIN_PK_20220922075031", log);
    ExceptionUtils.notNull(requestedURL, "SCIN_PK_20220922084850", log);

    return new Resource(requestUUID, LocalDateTime.now(), requestedURL, ProcessingStatus.REGISTERED);
  }

}
