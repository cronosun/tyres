package com.github.cronosun.tyres.implementation.basic_usage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class BasicUsageTest {

  @Test
  void basicUsageTest() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var locale = Locale.ENGLISH;

    assertEquals(
      "This is unformatted text.",
      resources.get(DemoBundle.class).unformattedText().get(locale)
    );
    assertEquals(
      "See the @Default annotation.",
      resources.get(DemoBundle.class).unformattedTextWithDefault().get(locale)
    );

    assertEquals(
      "I'm formatted (need to escape \"'\").",
      resources.get(DemoBundle.class).formattedTextNoArgument().get(locale)
    );
    assertEquals(
      "Hello from Claudia!",
      resources.get(DemoBundle.class).somethingWithArgument("Claudia").get(locale)
    );
    assertEquals(
      "Hello from Marcus Antonius!",
      resources.get(DemoBundle.class).somethingWithTwoArguments("Marcus", "Antonius").get(locale)
    );
    assertEquals(
      "Formatted with default says 'Howdy'!",
      resources.get(DemoBundle.class).formattedWithDefault("Howdy").get(locale)
    );

    assertEquals(
      "It's £178.99.",
      resources.get(DemoBundle.class).price(new BigDecimal("178.99")).get(Locale.UK)
    );
    assertEquals(
      "It's $178.99.",
      resources.get(DemoBundle.class).price(new BigDecimal("178.99")).get(Locale.US)
    );
    assertEquals(
      "It's $178.99.",
      resources.get(DemoBundle.class).price(new BigDecimal("178.99")).get(new Locale("en", "NZ"))
    );

    // if arguments implement Resolvable, they're resolved
    var resolvedArgumentsString = resources
      .get(DemoBundle.class)
      .argumentsAreResolved(
        resources.get(DemoBundle.class).formattedTextNoArgument(),
        resources.get(DemoBundle.class).unformattedText()
      )
      .get(locale);
    assertEquals(
      "This is argument #1 'I'm formatted (need to escape \"'\").', this ist argument #2: 'I'm formatted (need to escape \"'\").'.",
      resolvedArgumentsString
    );
  }

  @Test
  void basicUsageTestDe() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var locale = Locale.GERMAN;

    // if you need to get multiple entries at once, you can also assign the bundle to a local variable or class
    // instance field (...or anywhere else: It's constant for the entire lifetime of the 'resources' instance).
    var bundle = resources.get(DemoBundle.class);

    assertEquals("Das ist unformatierter Text.", bundle.unformattedText().get(locale));
    assertEquals("See the @Default annotation.", bundle.unformattedTextWithDefault().get(locale));

    assertEquals(
      "Ich bin formatiert (\"'\" muss escaped werden!).",
      bundle.formattedTextNoArgument().get(locale)
    );
    assertEquals("Hallo von Claudia!", bundle.somethingWithArgument("Claudia").get(locale));
    assertEquals(
      "Hallo von Marcus Antonius!",
      bundle.somethingWithTwoArguments("Marcus", "Antonius").get(locale)
    );
    assertEquals(
      "Formatted with default says 'Howdy'!",
      bundle.formattedWithDefault("Howdy").get(locale)
    );

    assertEquals(
      "Das kostet 178,99 €.",
      bundle.price(new BigDecimal("178.99")).get(new Locale("de", "DE"))
    );
    assertEquals(
      "Das kostet CHF 178.99.",
      bundle.price(new BigDecimal("178.99")).get(new Locale("de", "CH"))
    );

    // if arguments implement Resolvable, they're resolved
    var resolvedArgumentsString = bundle
      .argumentsAreResolved(bundle.formattedTextNoArgument(), bundle.unformattedText())
      .get(locale);
    assertEquals(
      "Das ist Argument #1 'Ich bin formatiert (\"'\" muss escaped werden!).', das hier ist Argument #2: 'Ich bin formatiert (\"'\" muss escaped werden!).'.",
      resolvedArgumentsString
    );
  }

  @Test
  void basicUsageBinary() throws IOException {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);

    var english = TestUtil.binToString(
      resources.get(DemoBundle.class).somethingBinary(),
      Locale.ENGLISH
    );
    assertEquals("I say colourful labour!", english);

    var usEnglish = TestUtil.binToString(
      resources.get(DemoBundle.class).somethingBinary(),
      Locale.US
    );
    assertEquals("I say colorful labor!", usEnglish);

    var german = TestUtil.binToString(
      resources.get(DemoBundle.class).somethingBinary(),
      Locale.GERMAN
    );
    assertEquals("Ich bin die deutsche Übersetzung!", german);

    // Will take the root file ('filename.txt') if there's no specific translation for it. If you don't like
    // this behaviour, don't add a root-file.
    var french = TestUtil.binToString(
      resources.get(DemoBundle.class).somethingBinary(),
      Locale.FRENCH
    );
    assertEquals("I say colourful labour! (from root)", french);
  }
}
