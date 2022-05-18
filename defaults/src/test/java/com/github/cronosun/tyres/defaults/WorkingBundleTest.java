package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Resources;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WorkingBundleTest {

  @Test
  void basicTestsWithDifferentLocales() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);

    var msgColourUk = source.msg(WorkingBundle.INSTANCE.colour(), Locale.UK);
    Assertions.assertEquals("Colour", msgColourUk);
    var msgColourDe = source.msg(WorkingBundle.INSTANCE.colour(), Locale.GERMAN);
    Assertions.assertEquals("Farbe", msgColourDe);
    var msgColourUs = source.msg(WorkingBundle.INSTANCE.colour(), Locale.US);
    Assertions.assertEquals("Color", msgColourUs);
  }

  @Test
  void inheritance() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);

    var msg = source.msg(WorkingBundle.INSTANCE.somethingFromParent(), Locale.UK);
    Assertions.assertEquals("Message from parent interface", msg);
  }

  @Test
  void inheritanceWithArg() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);

    var msg = source.msg(
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("Albert"),
      Locale.UK
    );
    Assertions.assertEquals("Hello, Albert!", msg);
  }

  @Test
  void fallbackMessage() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.FALLBACK);

    var msg = source.msg(
      WorkingBundle.INSTANCE.somethingThatCannotBeFound("The argument"),
      Locale.UK
    );
    Assertions.assertEquals(
      "{{com.github.cronosun.tyres.defaults.WorkingBundle::somethingThatCannotBeFound} [The argument]}",
      msg
    );
  }

  @Test
  void returnsNullIfCannotBeFound() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);

    var msg = source.maybeMsg(
      WorkingBundle.INSTANCE.somethingThatCannotBeFound("The argument"),
      Locale.UK
    );
    Assertions.assertNull(msg);
  }

  @Test
  void msgAsArgumentIsResolved() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);

    var monday = WorkingBundle.INSTANCE.monday();
    var friday = WorkingBundle.INSTANCE.friday();
    var date = convertLocalDateToDateUtc(LocalDate.of(2022, 5, 15));
    var message = WorkingBundle.INSTANCE.saySomethingAboutDaysOfTheWeek(friday, monday, date);

    var msgEn = source.msg(message, Locale.UK);
    var msgDe = source.msg(message, Locale.GERMAN);

    Assertions.assertEquals("Friday is much better than Monday; today is 15/05/2022.", msgEn);
    Assertions.assertEquals(
      "Freitag ist deutlich besser als Montag; heute ist 15.05.2022.",
      msgDe
    );
  }

  @Test
  void ifAnArgumentCannotBeResolvedTheEntireMessageCannotBeResolved() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);

    var monday = WorkingBundle.INSTANCE.monday();
    var doesNotExist = WorkingBundle.INSTANCE.somethingThatIsMissing();
    var date = convertLocalDateToDateUtc(LocalDate.of(2022, 5, 15));
    var message = WorkingBundle.INSTANCE.saySomethingAboutDaysOfTheWeek(
      monday,
      doesNotExist,
      date
    );

    var msgEn = source.maybeMsg(message, Locale.UK);
    var msgDe = source.maybeMsg(message, Locale.GERMAN);
    Assertions.assertNull(msgEn);
    Assertions.assertNull(msgDe);
  }

  @Test
  void umlautsWork() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);

    var somethingWithUmlauts = WorkingBundle.INSTANCE.somethingWithUmlauts();
    var msgEn = source.msg(somethingWithUmlauts, Locale.UK);
    var msgDe = source.msg(somethingWithUmlauts, Locale.GERMAN);

    Assertions.assertEquals("Bigger", msgEn);
    Assertions.assertEquals("Größer", msgDe);
  }

  @Test
  void testLocalizedMsg() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);
    var localizedMsg = LocalizedMsg
      .builder()
      .with(Locale.GERMAN, "ein farbenfrohes Bier")
      .with(Locale.ENGLISH, "a colourful beer")
      .with(Locale.US, "a colorful beer")
      .build();
    var msg = WorkingBundle.INSTANCE.wrapLocalizedMessage(localizedMsg);

    var msgDe = source.msg(msg, Locale.GERMAN);
    var msgEn = source.msg(msg, Locale.ENGLISH);
    var msgUs = source.msg(msg, Locale.US);
    // should fall back to 'ENGLISH'
    var msgCa = source.msg(msg, Locale.CANADA);
    // something that is not present
    var msgFr = source.maybeMsg(msg, Locale.FRENCH);

    Assertions.assertEquals("Der Text 'ein farbenfrohes Bier' wurde schon übersetzt.", msgDe);
    Assertions.assertEquals("The text 'a colourful beer' has already been localized.", msgEn);
    Assertions.assertEquals("The text 'a colorful beer' has already been localized.", msgUs);
    Assertions.assertEquals("The text 'a colourful beer' has already been localized.", msgCa);
    Assertions.assertNull(msgFr);
  }

  private static Date convertLocalDateToDateUtc(LocalDate localDate) {
    var utc = ZoneId.of("UTC");
    return Date.from(localDate.atStartOfDay(utc).toInstant());
  }
}
