package pl.pk.resources;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ExceptionUtilTest {

  private static final Logger log = LoggerFactory.getLogger(ExceptionUtilTest.class);

  @Test
  void checkNotNull() {

    final String scinMsg = "SCIN_PK_20220922080311";
    final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
        () -> ExceptionUtils.notNull(null, scinMsg, log));

    Assertions.assertTrue(exception.getMessage().contains(scinMsg));
  }

  @Test
  void checkNotEmpty() {

    final String scinMsg = "SCIN_PK_20220922082403";
    final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
        () -> ExceptionUtils.notEmpty("", scinMsg, log));

    Assertions.assertTrue(exception.getMessage().contains(scinMsg));
  }

  @Test
  void checkCanCreateURL() {

    final String scinIfNotCorrect = "SCIN_PK_20220922090836";
    final String correctURL = "http://google.com";
    final String correctURL2 = "https://www.onet.pl";

    Assertions.assertDoesNotThrow(() -> ExceptionUtils.canCreateURL(correctURL, "SCIN_PK_20220922090918", log));
    Assertions.assertDoesNotThrow(() -> ExceptionUtils.canCreateURL(correctURL2, "SCIN_PK_20220922090918", log));

    final String badUrl = "";
    final String badUrl2 = "www";
    final String badUrl3 = "http:www.google.pl";
    final String badUrl4 = "https://localhost";
    final String badUrl5 = "htp://google.com";

    final IllegalArgumentException exception1 = Assertions.assertThrows(IllegalArgumentException.class,
        () -> ExceptionUtils.canCreateURL(badUrl, scinIfNotCorrect, log));
    final IllegalArgumentException exception2 = Assertions.assertThrows(IllegalArgumentException.class,
        () -> ExceptionUtils.canCreateURL(badUrl2, scinIfNotCorrect, log));
    final IllegalArgumentException exception3 = Assertions.assertThrows(IllegalArgumentException.class,
        () -> ExceptionUtils.canCreateURL(badUrl3, scinIfNotCorrect, log));
    final IllegalArgumentException exception4 = Assertions.assertThrows(IllegalArgumentException.class,
        () -> ExceptionUtils.canCreateURL(badUrl4, scinIfNotCorrect, log));
    final IllegalArgumentException exception5 = Assertions.assertThrows(IllegalArgumentException.class,
        () -> ExceptionUtils.canCreateURL(badUrl5, scinIfNotCorrect, log));

    Assertions.assertTrue(exception1.getMessage().contains(scinIfNotCorrect));
    Assertions.assertTrue(exception2.getMessage().contains(scinIfNotCorrect));
    Assertions.assertTrue(exception3.getMessage().contains(scinIfNotCorrect));
    Assertions.assertTrue(exception4.getMessage().contains(scinIfNotCorrect));
    Assertions.assertTrue(exception5.getMessage().contains(scinIfNotCorrect));
  }

}
