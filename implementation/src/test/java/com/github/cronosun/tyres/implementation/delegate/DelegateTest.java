package com.github.cronosun.tyres.implementation.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * In your application you most likely want to extend {@link com.github.cronosun.tyres.core.Resources},
 * this shows how to do that.
 */
public class DelegateTest {

  @Test
  void testDelegateUsingLocalizedMsg() {
    var originalResources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var resources = new ExtendedResources(originalResources);
    var bundle = resources.get(ExtendedResourcesBundle.class);

    var msg = resources.toLocalized(bundle.sayHello());
    assertEquals(
      Map.of("de-DE", "Hallo!", "en-US", "Hello!", "fr-FR", "Salut!"),
      msg.serialized()
    );
  }

  @Test
  void testDelegateUsingValidate() {
    var originalResources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var resources = new ExtendedResources(originalResources);
    resources.validate(ExtendedResourcesBundle.class);
  }

  @Test
  void testDelegateUsingAsIntMethod() {
    var originalResources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var resources = new ExtendedResources(originalResources);
    var bundle = resources.get(ExtendedResourcesBundle.class);
    assertEquals(10, resources.asInt(bundle.holidayLeaveDaysPerYear(), Locale.US));
    assertEquals(30, resources.asInt(bundle.holidayLeaveDaysPerYear(), Locale.FRANCE));
    assertEquals(29, resources.asInt(bundle.holidayLeaveDaysPerYear(), Locale.GERMANY));
  }
}
