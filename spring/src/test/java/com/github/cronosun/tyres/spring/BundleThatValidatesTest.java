package com.github.cronosun.tyres.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.TyResException;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BundleThatValidatesTest {

  @Autowired
  Resources resources;

  @Test
  void bundleWorks() {
    var bundle = resources.get(BundleThatValidates.class);

    assertEquals("Hello, world!", bundle.sayHello().get(Locale.ENGLISH));
    assertEquals("Hello, Albert!", bundle.sayHelloTo("Albert").get(Locale.ENGLISH));

    assertEquals("Hallo, Welt!", bundle.sayHello().get(Locale.GERMAN));
    assertEquals("Hallo, Albert!", bundle.sayHelloTo("Albert").get(Locale.GERMAN));
  }

  @Test
  void bundleValidatesForEnAndDe() {
    resources.validate(BundleThatValidates.class, Set.of(Locale.ENGLISH, Locale.GERMAN));
  }

  @Test
  void bundleDoesNotValidateForIt() {
    assertThrows(
      TyResException.class,
      () -> resources.validate(BundleThatValidates.class, Locale.ITALIAN)
    );
  }
}
