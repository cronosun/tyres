package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;

public interface ValidatorBackend {
  /**
   * Validate when {@link Resources#validate(Class, Locale)} is called.
   */
  void validateManually(Resources resources, Class<?> bundleClass, Locale locale);

  /**
   * Validate when a new bundle is created, see {@link Resources#get(Class)}.
   */
  void validateOnBundleCreation(Resources resources2, Class<?> bundleClass, Locale locale);
}
