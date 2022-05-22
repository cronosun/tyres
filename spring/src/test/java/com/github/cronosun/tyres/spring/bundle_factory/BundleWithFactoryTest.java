package com.github.cronosun.tyres.spring.bundle_factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BundleWithFactoryTest {

  @Autowired
  BundleWithFactory bundleWithFactory;

  @Autowired
  Resources resources;

  @Test
  void contextLoads() {
    assertEquals(
      "File win32.dll not found.",
      resources.resolver().get(bundleWithFactory.noSuchFileError("win32.dll"), Locale.UK)
    );

    assertNotNull(bundleWithFactory);
  }
}
