package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.Resources2;
import java.util.Locale;

public interface ValidatorBackend {
  void validateManually(Resources2 resources, Class<?> bundleClass, Locale locale);
  void validateOnAccess(Resources2 resources2, Class<?> bundleClass, Locale locale);
}
