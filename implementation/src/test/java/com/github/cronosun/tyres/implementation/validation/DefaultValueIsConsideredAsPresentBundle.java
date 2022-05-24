package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

public interface DefaultValueIsConsideredAsPresentBundle {
  DefaultValueIsConsideredAsPresentBundle INSTANCE = TyRes.create(
    DefaultValueIsConsideredAsPresentBundle.class
  );

  @Default("Hello {0}!")
  MsgRes somethingThatIsNotInThePropertiesButThereIsADefaultValue(String name);
}
