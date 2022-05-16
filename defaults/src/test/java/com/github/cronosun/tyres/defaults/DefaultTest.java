package com.github.cronosun.tyres.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class DefaultTest {

  @Test
  void defaultValueIsNotUsedIfThereIsAValueInTheProperties() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);
    var msg = source.message(
      DefaultTestBundle.INSTANCE.somethingThatIsAlsoFoundInProperty(),
      Locale.ENGLISH
    );
    assertEquals("This is from the property", msg);
  }

  @Test
  void defaultValueIsUsedIfMissingInProperties() {
    var source = DefaultResources.newDefaultImplementation(Resources.NotFoundStrategy.THROW);
    var msg = source.message(
      DefaultTestBundle.INSTANCE.withConfiguredDefault("ABC"),
      Locale.ENGLISH
    );
    assertEquals("This is the message 'ABC'.", msg);
  }
}
