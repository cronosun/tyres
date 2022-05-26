package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Validation;
import com.github.cronosun.tyres.core.experiment.Fmt;

public interface OptionalResourcesBundle {
  @Validation(optional = true)
  Fmt thisMessageIsOptional(String name);
}
