package com.github.cronosun.tyres.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.cronosun.tyres.core.MsgSource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class WorkingBundleTest {

  @Test
  void basicTestsWithDifferentLocales() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var msgColourUk = source.message(WorkingBundle.INSTANCE.colour(), Locale.UK);
    assertEquals("Colour", msgColourUk);
    var msgColourDe = source.message(WorkingBundle.INSTANCE.colour(), Locale.GERMAN);
    assertEquals("Farbe", msgColourDe);
    var msgColourUs = source.message(WorkingBundle.INSTANCE.colour(), Locale.US);
    assertEquals("Color", msgColourUs);
  }

  @Test
  void inheritance() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var msg = source.message(WorkingBundle.INSTANCE.somethingFromParent(), Locale.UK);
    assertEquals("Message from parent interface", msg);
  }

  @Test
  void inheritanceWithArg() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var msg = source.message(
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("Albert"),
      Locale.UK
    );
    assertEquals("Hello, Albert!", msg);
  }

  @Test
  void fallbackMessage() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.FALLBACK);

    var msg = source.message(
      WorkingBundle.INSTANCE.somethingThatCannotBeFound("The argument"),
      Locale.UK
    );
    assertEquals(
      "{{com.github.cronosun.tyres.defaults.WorkingBundle::somethingThatCannotBeFound} [The argument]}",
      msg
    );
  }

  @Test
  void returnsNullIfCannotBeFound() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.FALLBACK);

    var msg = source.maybeMessage(
      WorkingBundle.INSTANCE.somethingThatCannotBeFound("The argument"),
      Locale.UK
    );
    assertNull(msg);
  }

  @Test
  void msgAsArgumentIsResolved() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var monday = WorkingBundle.INSTANCE.monday();
    var friday = WorkingBundle.INSTANCE.friday();
    var date = convertLocalDateToDateUtc(LocalDate.of(2022, 5, 15));
    var message = WorkingBundle.INSTANCE.saySomethingAboutDaysOfTheWeek(friday, monday, date);

    var msgEn = source.message(message, Locale.UK);
    var msgDe = source.message(message, Locale.GERMAN);

    assertEquals("Friday is much better than Monday; today is 15/05/2022.", msgEn);
    assertEquals("Freitag ist deutlich besser als Montag; heute ist 15.05.2022.", msgDe);
  }

  @Test
  void ifAnArgumentCannotBeResolvedTheEntireMessageCannotBeResolved() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var monday = WorkingBundle.INSTANCE.monday();
    var doesNotExist = WorkingBundle.INSTANCE.somethingThatIsMissing();
    var date = convertLocalDateToDateUtc(LocalDate.of(2022, 5, 15));
    var message = WorkingBundle.INSTANCE.saySomethingAboutDaysOfTheWeek(
      monday,
      doesNotExist,
      date
    );

    var msgEn = source.maybeMessage(message, Locale.UK);
    var msgDe = source.maybeMessage(message, Locale.GERMAN);
    assertNull(msgEn);
    assertNull(msgDe);
  }

  @Test
  void umlautsWork() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var somethingWithUmlauts = WorkingBundle.INSTANCE.somethingWithUmlauts();
    var msgEn = source.message(somethingWithUmlauts, Locale.UK);
    var msgDe = source.message(somethingWithUmlauts, Locale.GERMAN);

    assertEquals("Bigger", msgEn);
    assertEquals("Größer", msgDe);
  }

  private static Date convertLocalDateToDateUtc(LocalDate localDate) {
    var utc = ZoneId.of("UTC");
    return Date.from(localDate.atStartOfDay(utc).toInstant());
  }
}
