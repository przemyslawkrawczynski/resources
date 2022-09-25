package pl.pk.resources.bussiness;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pk.resources.ExceptionUtils;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RegisteredRequest {

  private static final Logger log = LoggerFactory.getLogger(RegisteredRequest.class);

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  @Type(type = "uuid-char")
  private UUID requestUUID;
  private String requestedURL;
  private LocalDateTime firstRegistrationDate;
  @Enumerated(EnumType.STRING)
  private RegisterStatus registerStatus;
  @Version
  private Integer version;

  private RegisteredRequest(final UUID requestUUID, final String requestedURL,
      final LocalDateTime firstRegistrationDate) {
    this.requestUUID = requestUUID;
    this.requestedURL = requestedURL;
    // Wynieść timeProvider
    this.firstRegistrationDate = firstRegistrationDate;
    this.registerStatus = RegisterStatus.REGISTERED;
  }

  public static RegisteredRequest register(final UUID requestUUID, final String requestedURL,
      final LocalDateTime firstRegistrationDate) {
    ExceptionUtils.notNull(requestUUID, "SCIN_PK_20220924184322", log);
    ExceptionUtils.notEmpty(requestedURL, "SCIN_PK_20220924184409", log);
    ExceptionUtils.canCreateURL(requestedURL, "SCIN_PK_20220924184421", log);
    ExceptionUtils.notNull(firstRegistrationDate, "SCIN_PK_20220924202431", log);

    return new RegisteredRequest(requestUUID, requestedURL, firstRegistrationDate);
  }
}
