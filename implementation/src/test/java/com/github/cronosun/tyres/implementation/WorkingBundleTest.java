package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.LocalizedMsg;
import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WorkingBundleTest {

  private static Date convertLocalDateToDateUtc(LocalDate localDate) {
    var utc = ZoneId.of("UTC");
    return Date.from(localDate.atStartOfDay(utc).toInstant());
  }

  @Test
  void basicTestsWithDifferentLocales() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var msgColourUk = source.msg().get(WorkingBundle.INSTANCE.colour(), Locale.UK);
    Assertions.assertEquals("Colour", msgColourUk);
    var msgColourDe = source.msg().get(WorkingBundle.INSTANCE.colour(), Locale.GERMAN);
    Assertions.assertEquals("Farbe", msgColourDe);
    var msgColourUs = source.msg().get(WorkingBundle.INSTANCE.colour(), Locale.US);
    Assertions.assertEquals("Color", msgColourUs);
  }

  @Test
  void inheritance() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var msg = source.msg().get(WorkingBundle.INSTANCE.somethingFromParent(), Locale.UK);
    Assertions.assertEquals("Message from parent interface", msg);
  }

  @Test
  void inheritanceWithArg() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var msg = source
      .msg()
      .get(WorkingBundle.INSTANCE.somethingFromParentWithArgument("Albert"), Locale.UK);
    Assertions.assertEquals("Hello, Albert!", msg);
  }

  @Test
  void fallbackMessage() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.FALLBACK);

    var msg = source
      .msg()
      .get(WorkingBundle.INSTANCE.somethingThatCannotBeFound("The argument"), Locale.UK);
    Assertions.assertEquals(
      "(((com.github.cronosun.tyres.implementation.WorkingBundle) somethingThatCannotBeFound) ['The argument'])",
      msg
    );
  }

  @Test
  void returnsNullIfCannotBeFound() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var msg = source
      .msg()
      .maybe(WorkingBundle.INSTANCE.somethingThatCannotBeFound("The argument"), Locale.UK);
    Assertions.assertNull(msg);
  }

  @Test
  void msgAsArgumentIsResolved() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var monday = WorkingBundle.INSTANCE.monday();
    var friday = WorkingBundle.INSTANCE.friday();
    var date = convertLocalDateToDateUtc(LocalDate.of(2022, 5, 15));
    var message = WorkingBundle.INSTANCE.saySomethingAboutDaysOfTheWeek(friday, monday, date);

    var msgEn = source.msg().get(message, Locale.UK);
    var msgDe = source.msg().get(message, Locale.GERMAN);

    Assertions.assertEquals("Friday is much better than Monday; today is 15/05/2022.", msgEn);
    Assertions.assertEquals(
      "Freitag ist deutlich besser als Montag; heute ist 15.05.2022.",
      msgDe
    );
  }

  @Test
  void ifAnArgumentCannotBeResolvedTheEntireMessageCannotBeResolved() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var monday = WorkingBundle.INSTANCE.monday();
    var doesNotExist = WorkingBundle.INSTANCE.somethingThatIsMissing();
    var date = convertLocalDateToDateUtc(LocalDate.of(2022, 5, 15));
    var message = WorkingBundle.INSTANCE.saySomethingAboutDaysOfTheWeek(
      monday,
      doesNotExist,
      date
    );

    var msgEn = source.msg().maybe(message, Locale.UK);
    var msgDe = source.msg().maybe(message, Locale.GERMAN);
    Assertions.assertNull(msgEn);
    Assertions.assertNull(msgDe);
  }

  @Test
  void umlautsWork() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var somethingWithUmlauts = WorkingBundle.INSTANCE.somethingWithUmlauts();
    var msgEn = source.msg().get(somethingWithUmlauts, Locale.UK);
    var msgDe = source.msg().get(somethingWithUmlauts, Locale.GERMAN);

    Assertions.assertEquals("Bigger", msgEn);
    Assertions.assertEquals("Größer", msgDe);
  }

  @Test
  void testLocalizedMsg() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    var localizedMsg = LocalizedMsg
      .builder()
      .with(Locale.GERMAN, "ein farbenfrohes Bier")
      .with(Locale.ENGLISH, "a colourful beer")
      .with(Locale.US, "a colorful beer")
      .build();
    var msg = WorkingBundle.INSTANCE.wrapLocalizedMessage(localizedMsg);

    var msgDe = source.msg().get(msg, Locale.GERMAN);
    var msgEn = source.msg().get(msg, Locale.ENGLISH);
    var msgUs = source.msg().get(msg, Locale.US);
    // should fall back to 'ENGLISH'
    var msgCa = source.msg().get(msg, Locale.CANADA);
    // something that is not present
    var msgFr = source.msg().maybe(msg, Locale.FRENCH);

    Assertions.assertEquals("Der Text 'ein farbenfrohes Bier' wurde schon übersetzt.", msgDe);
    Assertions.assertEquals("The text 'a colourful beer' has already been localized.", msgEn);
    Assertions.assertEquals("The text 'a colorful beer' has already been localized.", msgUs);
    Assertions.assertEquals("The text 'a colourful beer' has already been localized.", msgCa);
    Assertions.assertNull(msgFr);
  }
}
