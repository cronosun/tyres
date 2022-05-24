package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

public interface ValidatorAlsoDetectsErrorsInDefaultValues {
  ValidatorAlsoDetectsErrorsInDefaultValues INSTANCE = TyRes.create(
    ValidatorAlsoDetectsErrorsInDefaultValues.class
  );

  @Default("Hello {0} ---> too many arguments: {1}")
  MsgRes thereIsSomethingWrongWithTheMessagePattern(String name);
}
