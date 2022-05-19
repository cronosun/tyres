package com.github.cronosun.tyres.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;
import org.junit.jupiter.api.Test;

public class LocalizedMsgTest {

  @Test
  void testFindsCorrectResultsWithoutRoot() {
    var msg = LocalizedMsg
      .builder()
      .with(Locale.GERMAN, "Hallo, Welt!")
      .with(Locale.ENGLISH, "Hello, world!")
      .with(Locale.CANADA, "This is a boat the world!")
      .build();

    // direct results
    assertEquals("Hallo, Welt!", msg.message(Locale.GERMAN));
    assertEquals("Hello, world!", msg.message(Locale.ENGLISH));
    assertEquals("This is a boat the world!", msg.message(Locale.CANADA));

    // nothing matches
    assertNull(msg.message(Locale.FRENCH));
    assertNull(msg.message(Locale.SIMPLIFIED_CHINESE));
    assertNull(msg.message(Locale.ITALIAN));

    // with candidates
    assertEquals("Hallo, Welt!", msg.message(Locale.GERMANY));
    assertEquals("Hallo, Welt!", msg.message(new Locale("de", "CH")));
    assertEquals("Hallo, Welt!", msg.message(new Locale("de", "AT")));
    assertEquals("Hello, world!", msg.message(Locale.UK));
    assertEquals("Hello, world!", msg.message(Locale.US));
    assertEquals("This is a boat the world!", msg.message(Locale.CANADA));
  }

  @Test
  void testFindsCorrectResultsWithRoot() {
    var msg = LocalizedMsg
      .builder()
      .with(Locale.GERMAN, "Hallo, Welt!")
      .with(Locale.ENGLISH, "Hello, world!")
      .with(Locale.ROOT, "Hola")
      .build();

    // root matches
    assertEquals("Hola", msg.message(Locale.FRENCH));
    assertEquals("Hola", msg.message(Locale.SIMPLIFIED_CHINESE));
    assertEquals("Hola", msg.message(Locale.ITALIAN));

    // with candidates
    assertEquals("Hallo, Welt!", msg.message(Locale.GERMANY));
    assertEquals("Hallo, Welt!", msg.message(new Locale("de", "CH")));
    assertEquals("Hallo, Welt!", msg.message(new Locale("de", "AT")));
    assertEquals("Hello, world!", msg.message(Locale.UK));
    assertEquals("Hello, world!", msg.message(Locale.US));
  }
}
