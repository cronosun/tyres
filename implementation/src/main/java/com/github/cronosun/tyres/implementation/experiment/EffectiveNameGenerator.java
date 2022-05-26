package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.experiment.ResInfo;

public interface EffectiveNameGenerator extends ResInfo.EffectiveNameGenerator {
  BaseName effectiveBaseName(Class<?> bundleClass, BaseName declaredBaseName);

  /**
   * Returns the empty implementation: The empty implementation does nothing: It does not rewrite names.
   */
  static EffectiveNameGenerator empty() {
    return NoOpEffectiveNameGenerator.instance();
  }
}
