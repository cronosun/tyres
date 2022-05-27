package com.github.cronosun.tyres.implementation.static_translation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Static translations (where the `Resolvable` is created in a static context) are possible. Whether that's a good
 * design or not is up to you.
 */
public class StaticTranslationTest {

  @Test
  void enumTranslation() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);

    assertEquals("Door is closed", translateEnum(resources, DoorInfo.CLOSED, Locale.ENGLISH));
    assertEquals(
      "Door is fucked up beyond all recognition",
      translateEnum(resources, DoorInfo.FUBAR, Locale.ENGLISH)
    );
    assertEquals("Door is open", translateEnum(resources, DoorInfo.OPEN, Locale.US));
  }

  @Test
  void validationExceptionTest() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    try {
      throwValidationException();
    } catch (ValidationException exception) {
      var translated = translateValidationException(resources, exception, Locale.ENGLISH);
      assertEquals("Given amount is too large.", translated);

      // it's also possible to compare the resolvable (they're always constant).
      var given = resources.resolve(exception.message());
      var expected = resources
        .get(StaticTranslationBundle.class)
        .validationGivenAmountIsTooLarge();
      assertEquals(expected, given);
    }
  }

  private void throwValidationException() {
    throw ValidationException.newAmountTooLargeValidationException();
  }

  private String translateEnum(Resources resources, DoorInfo doorInfo, Locale locale) {
    return resources.resolve(doorInfo.display()).get(locale);
  }

  private String translateValidationException(
    Resources resources,
    ValidationException exception,
    Locale locale
  ) {
    return resources.resolve(exception.message()).get(locale);
  }
}
