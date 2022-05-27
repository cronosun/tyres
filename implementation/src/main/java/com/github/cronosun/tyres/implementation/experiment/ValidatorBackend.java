package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;

public interface ValidatorBackend {
  void validateManually(Resources resources, Class<?> bundleClass, Locale locale);
  void validateOnAccess(Resources resources2, Class<?> bundleClass, Locale locale);
}
