package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

public interface InvalidPatternBundle {
  InvalidPatternBundle INSTANCE = TyRes.create(InvalidPatternBundle.class);

  MsgRes somethingWithInvalidPattern(String argument0);
}
