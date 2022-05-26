package com.github.cronosun.tyres.implementation.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;

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
    var bundle = resources.get(ExtendedResourcesBundle.class);
    resources.validate(ExtendedResourcesBundle.INSTANCE);
  }

  @Test
  void testDelegateUsingAsIntMethod() {
    var originalResources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);
    var resources = new ExtendedResources(originalResources);
    assertEquals(
      10,
      resources.asInt(ExtendedResourcesBundle.INSTANCE.holidayLeaveDaysPerYear(), Locale.US)
    );
    assertEquals(
      30,
      resources.asInt(ExtendedResourcesBundle.INSTANCE.holidayLeaveDaysPerYear(), Locale.FRANCE)
    );
    assertEquals(
      29,
      resources.asInt(ExtendedResourcesBundle.INSTANCE.holidayLeaveDaysPerYear(), Locale.GERMANY)
    );
  }
}
