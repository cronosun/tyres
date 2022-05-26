package com.github.cronosun.tyres.implementation.localized_msg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.cronosun.tyres.core.LocalizedMsg;
import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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

  @Test
  void testLocalizedMsgSerialization() {
    // useful if you want to send the localized message to the client (e.g. using Jackson).
    var msg = LocalizedMsg
      .builder()
      .with(Locale.GERMAN, "Hallo, Welt!")
      .with(Locale.ENGLISH, "Hello, world!")
      .with(Locale.ROOT, "Hola")
      .build();
    var serialized = msg.serialized();
    assertEquals(Map.of("und", "Hola", "de", "Hallo, Welt!", "en", "Hello, world!"), serialized);
  }

  @Test
  void testLocalizedMsgDeserialization() {
    var serialized = Map.of("und", "Hola", "de", "Hallo, Welt!", "en", "Hello, world!");
    var msg = LocalizedMsg.deserialize(serialized);

    assertEquals(Set.of(Locale.GERMAN, Locale.ENGLISH, Locale.ROOT), msg.availableLocales());
    assertEquals("Hallo, Welt!", msg.message(Locale.GERMAN));
    assertEquals("Hello, world!", msg.message(Locale.ENGLISH));
    assertEquals("Hola", msg.message(Locale.ROOT));
  }

  @Test
  void testFromResourcesMessage() {
    var resources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);
    var msg = LocalizedMsg.fromResources(
      resources,
      LocalizedMsgBundle.INSTANCE.sayHello("Simon"),
      LocalizedMsg.FromResourcesConfig.THROW,
      Set.of(Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN)
    );

    assertEquals(
      Map.of("de", "Hallo, Simon!", "en", "Hello, Simon!", "fr", "Salut, Simon!"),
      msg.serialized()
    );
  }

  @Test
  void testFromResourcesStr() {
    var resources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);
    var msg = LocalizedMsg.fromResources(
      resources,
      LocalizedMsgBundle.INSTANCE.colour(),
      LocalizedMsg.FromResourcesConfig.THROW,
      Set.of(Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN)
    );

    assertEquals(Map.of("de", "Farbe", "en", "Colour", "fr", "Couleur"), msg.serialized());
  }

  @Test
  void testFromResourcesOptional() {
    var resources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);
    var msg = LocalizedMsg.fromResources(
      resources,
      LocalizedMsgBundle.INSTANCE.messageNotPresentForSomeLocales(),
      LocalizedMsg.FromResourcesConfig.MAYBE,
      Set.of(Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN)
    );
    // only present for English
    assertEquals(Map.of("en", "For English, it's present."), msg.serialized());
  }
}
