package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;

public interface ValidatorBackend {
  void validate(When when, Class<?> bundleClass, Locale locale);

  enum When {
    /**
     * Validate when {@link Resources#validate(Class, Locale)} is called or in other situations. All validators
     * should validate in that case.
     */
    OTHER,
    /**
     * Validate when the bundle is used, for example when somebody calls
     * {@link com.github.cronosun.tyres.core.Text#get(Locale)}. Most validators do nothing in that case.
     */
    ON_USE,
  }
}
