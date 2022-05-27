package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.Fmt;

public interface DefaultValueIsConsideredAsPresentBundle {
  @Default("Hello {0}!")
  Fmt somethingThatIsNotInThePropertiesButThereIsADefaultValue(String name);
}
