package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.experiment.Fmt;

public interface ValidatorAlsoDetectsErrorsInDefaultValues {
  @Default("Hello {0} ---> too many arguments: {1}")
  Fmt thereIsSomethingWrongWithTheMessagePattern(String name);
}
