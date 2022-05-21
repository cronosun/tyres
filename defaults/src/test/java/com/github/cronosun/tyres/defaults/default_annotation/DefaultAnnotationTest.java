package com.github.cronosun.tyres.defaults.default_annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.defaults.Implementation;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link com.github.cronosun.tyres.core.Default} annotation.
 */
public class DefaultAnnotationTest {

  @Test
  void defaultValueIsNotUsedIfThereIsAValueInTheProperties() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    var msg = resources
      .msg()
      .get(
        DefaultAnnotationTestBundle.INSTANCE.somethingThatIsAlsoFoundInProperty(),
        Locale.ENGLISH
      );
    assertEquals("This is from the property", msg);
  }

  @Test
  void defaultValueIsNotUsedIfThereIsAValueInThePropertiesForStringRes() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    var msg = resources
      .str()
      .get(
        DefaultAnnotationTestBundle.INSTANCE.stringResourceThatIsAlsoFoundInProperty(),
        Locale.ENGLISH
      );
    assertEquals("I am a string from the property file", msg);
  }

  @Test
  void defaultValueIsUsedIfMissingInProperties() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    var msg = resources
      .msg()
      .get(DefaultAnnotationTestBundle.INSTANCE.withConfiguredDefault("ABC"), Locale.ENGLISH);
    assertEquals("This is the message 'ABC'.", msg);
  }

  @Test
  void defaultValueIsUsedIfMissingInPropertiesForString() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    var msg = resources
      .str()
      .get(DefaultAnnotationTestBundle.INSTANCE.stringResWithConfiguredDefault(), Locale.ENGLISH);
    assertEquals("Yes, this is the string to use", msg);
  }
}
