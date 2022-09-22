package pl.pk.resources;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtils {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

  public static void notNull(final Object object, final String scin, final Logger log) {
    if (object == null) {
      throwInvalidArgumentException(scin, log);
    }
  }

  public static void notEmpty(final CharSequence object, final String scin, final Logger log) {
    if (StringUtils.isEmpty(object)) {
      throwInvalidArgumentException(scin, log);
    }
  }

  public static void canCreateURL(final String url, final String scin, final Logger log) {
    ExceptionUtils.notNull(url, "SCIN_PK_20220922093841", logger);
    ExceptionUtils.notNull(scin, "SCIN_PK_20220922093844", logger);
    ExceptionUtils.notNull(log, "SCIN_PK_20220922094003", logger);

    final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
    if (!urlValidator.isValid(url))
      throwInvalidArgumentException(scin, log);
  }

  private static void throwInvalidArgumentException(final String scin, final Logger log) {
    final IllegalArgumentException e = new IllegalArgumentException(scin);
    ExceptionUtils.throwException(e, scin, log);
  }

  private static void throwException(final RuntimeException e, final String scin, final Logger log) {
    log.error(scin, e);
    throw e;
  }
}
