package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.core.TyRes;

/**
 * Patterns are only validated for {@link com.github.cronosun.tyres.core.MsgRes}, not for {@link StrRes}.
 */
public interface InvalidPatternInStrResBundle {
  InvalidPatternInStrResBundle INSTANCE = TyRes.create(InvalidPatternInStrResBundle.class);

  StrRes invalidPattern1();

  StrRes invalidPattern2();

  @Default("Is is invalid {0x,??}, {1}")
  StrRes invalidPattern3();

  @Default("Is is invalid {0,,_ {1}")
  StrRes invalidPattern4();
}
