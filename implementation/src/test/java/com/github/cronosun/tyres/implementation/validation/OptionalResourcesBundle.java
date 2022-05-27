package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Validation;

public interface OptionalResourcesBundle {
  @Validation(optional = true)
  Fmt thisMessageIsOptional(String name);
}
