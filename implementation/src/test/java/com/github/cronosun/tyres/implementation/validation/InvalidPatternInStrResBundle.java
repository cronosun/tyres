package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.Text;

/**
 * Patterns are only validated for {@link com.github.cronosun.tyres.core.Fmt}, not for {@link Text}.
 */
public interface InvalidPatternInStrResBundle {
  Text invalidPattern1();

  Text invalidPattern2();

  @Default("Is is invalid {0x,??}, {1}")
  Text invalidPattern3();

  @Default("Is is invalid {0,,_ {1}")
  Text invalidPattern4();
}
