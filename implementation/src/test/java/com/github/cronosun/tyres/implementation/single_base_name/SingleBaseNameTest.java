package com.github.cronosun.tyres.implementation.single_base_name;

import static org.junit.jupiter.api.Assertions.*;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.implementation.EffectiveNameGenerator;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.io.IOException;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * By using {@link com.github.cronosun.tyres.implementation.EffectiveNameGenerator}, you can
 * control the {@link com.github.cronosun.tyres.core.BaseName} and have all resources in one
 * single properties-file.
 */
public class SingleBaseNameTest {

  private static final BaseName SINGLE_BASE_NAME = BaseName.fromPackageAndName(
    "",
    "SingleBaseName"
  );

  private static Resources newResources() {
    return TestUtil.newInstanceWithEffectiveNameGenerator(
      DefaultNotFoundConfig.THROW,
      EffectiveNameGenerator.singleBaseName(SINGLE_BASE_NAME, SINGLE_BASE_NAME)
    );
  }

  @Test
  void testCanGetTextsAndBinary() throws IOException {
    var resources = newResources();
    var bundle = resources.get(SingleBaseNameOkBundle.class);

    var msg1 = bundle.message1().get(Locale.ROOT);
    assertEquals("Message one", msg1);
    var msg2 = bundle.message2().get(Locale.ROOT);
    assertEquals("Message two", msg2);
    var file = bundle.myFile();
    var stringFromFile = TestUtil.binToString(file, Locale.ROOT);
    assertEquals("This is 'my_file'.", stringFromFile);
    var stringFromFileDe = TestUtil.binToString(file, Locale.GERMAN);
    assertEquals("Das ist 'my_file'.", stringFromFileDe);
  }

  @Test
  void testValidBundleStillValidates() {
    var resources = newResources();
    // this makes sure that the implementation still knows what resources in the single bundle
    // belong to the original bundle (interface).
    resources.validate(SingleBaseNameOkBundle.class, Locale.ROOT);
  }

  @Test
  void validatorDetectsMissingResources() {
    var resources = newResources();
    var bundle = resources.get(WithResourcesNotFoundBundle.class);

    var msg1 = bundle.message1().get(Locale.ROOT);
    assertEquals("This is present", msg1);
    // this resource is missing
    var msg2 = bundle.message2().maybe(Locale.ROOT);
    assertNull(msg2);

    // message2 is missing and not declared as optional. The validator should detect that.
    assertThrows(
      TyResException.class,
      () -> resources.validate(WithResourcesNotFoundBundle.class, Locale.ROOT)
    );
  }

  @Test
  void validatorDetectsSuperfluousResources() {
    var resources = newResources();
    var bundle = resources.get(SuperfluousMessageBundle.class);

    var msg1 = bundle.message1().get(Locale.ROOT);
    assertEquals("Message1", msg1);

    // 'message2' is in the '.properties'-file but not in the interface. Validator should detect that.
    assertThrows(
      TyResException.class,
      () -> resources.validate(SuperfluousMessageBundle.class, Locale.ROOT)
    );
  }

  @Test
  void validatorDetectsMissingFile() {
    var resources = newResources();
    var bundle = resources.get(FileNotFoundBundle.class);
    var file = bundle.thisFileDoesNotExist().maybe(Locale.ROOT);
    assertNull(file);
    assertThrows(
      TyResException.class,
      () -> resources.validate(FileNotFoundBundle.class, Locale.ROOT)
    );
  }
}
