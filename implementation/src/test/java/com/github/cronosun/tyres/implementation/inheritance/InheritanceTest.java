package com.github.cronosun.tyres.implementation.inheritance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class InheritanceTest {

  @Test
  void bundleValidates() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    resources.validate(TheBundle.class, Locale.GERMAN);
    resources.validate(TheBundle.class, Locale.ENGLISH);
  }

  @Test
  void bundleResultsInCorrectStrings() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(TheBundle.class);
    var locale = Locale.US;

    // values declared in parent
    assertEquals(
      "Message declared in parent: Hello",
      bundle.somethingFromParent("Hello").get(locale)
    );
    assertEquals("Something else from parent", bundle.anotherThingFromParent().get(locale));
    assertEquals("Renamed in parent", bundle.thisIsRenamedInParent().get(locale));
    assertEquals(
      "Rename is effective",
      bundle.stringWithOverwrittenRenameInSubInterface().get(locale)
    );
    assertEquals("Default value from parent 1", bundle.withDefaultAnnotation1().get(locale));
    assertEquals("Default value from sub-interface", bundle.withDefaultAnnotation2().get(locale));

    // values declared in sub-interface
    assertEquals("Another message", bundle.anotherMessage().get(locale));
    assertEquals("Another string", bundle.anotherString().get(locale));
    assertEquals("this is the default value", bundle.somethingWithDefault().get(locale));
    assertEquals("Has been renamed", bundle.somethingIsWrongWithThisName().get(locale));
  }
}
