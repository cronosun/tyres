package com.github.cronosun.tyres.implementation.resolvable_list;

import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface ResolvableListBundle {
  Text colour();

  Fmt somethingWithArgument(String argument);

  Text aluminium();
}
