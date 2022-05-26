package com.github.cronosun.tyres.implementation.default_annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link com.github.cronosun.tyres.core.Default} annotation.
 */
public class DefaultAnnotationTest {

  @Test
  void defaultValueIsNotUsedIfThereIsAValueInTheProperties() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(DefaultAnnotationTestBundle.class);

    var msg = bundle.somethingThatIsAlsoFoundInProperty().get(Locale.ENGLISH);
    assertEquals("This is from the property", msg);
  }

  @Test
  void defaultValueIsNotUsedIfThereIsAValueInThePropertiesForStringRes() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(DefaultAnnotationTestBundle.class);

    var msg = bundle.stringResourceThatIsAlsoFoundInProperty().get(
        Locale.ENGLISH
      );
    assertEquals("I am a string from the property file", msg);
  }

  @Test
  void defaultValueIsUsedIfMissingInProperties() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(DefaultAnnotationTestBundle.class);
    var msg = bundle.withConfiguredDefault("ABC").get(Locale.ENGLISH);
    assertEquals("This is the message 'ABC'.", msg);
  }

  @Test
  void defaultValueIsUsedIfMissingInPropertiesForString() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(DefaultAnnotationTestBundle.class);
    var msg = bundle.stringResWithConfiguredDefault().get(Locale.ENGLISH);
    assertEquals("Yes, this is the string to use", msg);
  }
}
