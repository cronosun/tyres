package com.github.cronosun.tyres.implementation.validate_on_bundle_use;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class ValidateOnBundleUseTest {

  @Test
  void bundleIsNotValidatedOnUse() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(ValidateOnBundleUseBundle.class);

    // this is no problem, the GERMAN and ITALIAN bundles are not completely valid (but we can use what is
    // valid, since the bundle is not validated).
    assertEquals("I'm valid", bundle.alwaysValid().get(Locale.ENGLISH));
    assertEquals("Ich bin gültig", bundle.alwaysValid().get(Locale.GERMAN));
    assertEquals("Valid (IT)", bundle.alwaysValid().get(Locale.ITALIAN));
  }

  @Test
  void entireBundleIsValidatedOnUse() {
    var resources = TestUtil.newInstanceValidateOnUse(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(ValidateOnBundleUseBundle.class);

    // In this case however, we can only get the english text, since the entire bundle is validated (and this
    // will detect the errors in 'myMessage' in the GERMAN und ITALIAN bundles).
    assertEquals("I'm valid", bundle.alwaysValid().get(Locale.ENGLISH));
    assertThrows(TyResException.class, () -> bundle.alwaysValid().get(Locale.GERMAN));
    assertThrows(TyResException.class, () -> bundle.alwaysValid().get(Locale.ITALIAN));
  }
}
