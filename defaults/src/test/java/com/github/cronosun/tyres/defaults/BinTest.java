package com.github.cronosun.tyres.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class BinTest {

  @Test
  void testNoLocalization() throws IOException {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var root = resources.bin(BinTestBundle.INSTANCE.resourceNoLocalization(), Locale.ROOT);
    assertEquals("Content from no_localization.txt", toString(root));
    var de = resources.bin(BinTestBundle.INSTANCE.resourceNoLocalization(), Locale.GERMAN);
    assertEquals("Content from no_localization.txt", toString(de));
    var us = resources.bin(BinTestBundle.INSTANCE.resourceNoLocalization(), Locale.US);
    assertEquals("Content from no_localization.txt", toString(us));
  }

  @Test
  void testLocalized() throws IOException {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var root = resources.bin(BinTestBundle.INSTANCE.resourceLocalized(), Locale.ROOT);
    assertEquals("The default.", toString(root));
    var de = resources.bin(BinTestBundle.INSTANCE.resourceLocalized(), Locale.GERMAN);
    assertEquals("Das hier ist Deutsch.", toString(de));
    var us = resources.bin(BinTestBundle.INSTANCE.resourceLocalized(), Locale.US);
    assertEquals("Aluminum in file!", toString(us));
    var en = resources.bin(BinTestBundle.INSTANCE.resourceLocalized(), Locale.ENGLISH);
    assertEquals("English from file.", toString(en));
  }

  @Test
  void testOnlyGerman() throws IOException {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    var root = resources.maybeBin(BinTestBundle.INSTANCE.onlyGerman(), Locale.ROOT);
    assertNull(root);
    var de = resources.bin(BinTestBundle.INSTANCE.onlyGerman(), Locale.GERMAN);
    assertEquals("Nur auf Deutsch verfügbar.", toString(de));
    var us = resources.maybeBin(BinTestBundle.INSTANCE.onlyGerman(), Locale.US);
    assertNull(us);
    var en = resources.maybeBin(BinTestBundle.INSTANCE.onlyGerman(), Locale.ENGLISH);
    assertNull(en);
  }

  String toString(InputStream stream) throws IOException {
    final byte[] bytes;
    try (InputStream is = stream) {
      bytes = is.readAllBytes();
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
