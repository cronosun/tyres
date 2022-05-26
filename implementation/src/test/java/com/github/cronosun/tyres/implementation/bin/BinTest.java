package com.github.cronosun.tyres.implementation.bin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class BinTest {

  @Test
  void testNoLocalization() throws IOException {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(BinTestBundle.class);

    var root = bundle.resourceNoLocalization().get(Locale.ROOT);
    assertEquals("Content from no_localization.txt", toString(root));
    var de = bundle.resourceNoLocalization().get(Locale.GERMAN);
    assertEquals("Content from no_localization.txt", toString(de));
    var us = bundle.resourceNoLocalization().get(Locale.US);
    assertEquals("Content from no_localization.txt", toString(us));
  }

  @Test
  void testLocalized() throws IOException {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(BinTestBundle.class);
    var resourceLocalized = bundle.resourceLocalized();

    var root = resourceLocalized.get(Locale.ROOT);
    assertEquals("The default.", toString(root));
    var de = resourceLocalized.get(Locale.GERMAN);
    assertEquals("Das hier ist Deutsch.", toString(de));
    var us = resourceLocalized.get(Locale.US);
    assertEquals("Aluminum in file!", toString(us));
    var en = resourceLocalized.get(Locale.ENGLISH);
    assertEquals("English from file.", toString(en));
  }

  @Test
  void testOnlyGerman() throws IOException {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(BinTestBundle.class);
    var onlyGerman = bundle.onlyGerman();

    var root = onlyGerman.maybe(Locale.ROOT);
    assertNull(root);
    var de = onlyGerman.get(Locale.GERMAN);
    assertEquals("Nur auf Deutsch verfügbar.", toString(de));
    var us = onlyGerman.maybe(Locale.US);
    assertNull(us);
    var en = onlyGerman.maybe(Locale.ENGLISH);
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
