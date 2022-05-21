package com.github.cronosun.tyres.defaults;

import java.util.Locale;
import java.util.Set;

public interface Validator {
  ValidationErrors validationErrors(Object bundle, Set<Locale> locales);

  default void validate(Object bundle, Set<Locale> locales) {
    validationErrors(bundle, locales).throwIfHasErrors();
  }

  static Validator newDefaultImplementation(StrBackend strBackend, BinBackend binBackend) {
    return new DefaultValidator(strBackend, binBackend);
  }
}
