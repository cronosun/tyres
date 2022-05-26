package com.github.cronosun.tyres.implementation.inheritance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class InheritanceTest {

  @Test
  void bundleValidates() {
    var resources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);
    assertNull(
      resources.common().validate(TheBundle.INSTANCE, Set.of(Locale.GERMAN, Locale.ENGLISH))
    );
  }

  @Test
  void bundleResultsInCorrectStrings() {
    var resources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);
    var locale = Locale.US;

    // values declared in parent
    assertEquals(
      "Message declared in parent: Hello",
      resources.msg().get(TheBundle.INSTANCE.somethingFromParent("Hello"), locale)
    );
    assertEquals(
      "Something else from parent",
      resources.str().get(TheBundle.INSTANCE.anotherThingFromParent(), locale)
    );
    assertEquals(
      "Renamed in parent",
      resources.str().get(TheBundle.INSTANCE.thisIsRenamedInParent(), locale)
    );
    assertEquals(
      "Rename is effective",
      resources.str().get(TheBundle.INSTANCE.stringWithOverwrittenRenameInSubInterface(), locale)
    );
    assertEquals(
      "Default value from parent 1",
      resources.str().get(TheBundle.INSTANCE.withDefaultAnnotation1(), locale)
    );
    assertEquals(
      "Default value from sub-interface",
      resources.str().get(TheBundle.INSTANCE.withDefaultAnnotation2(), locale)
    );

    // values declared in sub-interface
    assertEquals(
      "Another message",
      resources.msg().get(TheBundle.INSTANCE.anotherMessage(), locale)
    );
    assertEquals(
      "Another string",
      resources.str().get(TheBundle.INSTANCE.anotherString(), locale)
    );
    assertEquals(
      "this is the default value",
      resources.str().get(TheBundle.INSTANCE.somethingWithDefault(), locale)
    );
    assertEquals(
      "Has been renamed",
      resources.str().get(TheBundle.INSTANCE.somethingIsWrongWithThisName(), locale)
    );
  }
}
