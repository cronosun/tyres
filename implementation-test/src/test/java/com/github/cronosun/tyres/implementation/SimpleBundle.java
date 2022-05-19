package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;

public interface SimpleBundle {
  SimpleBundle INSTANCE = TyRes.create(SimpleBundle.class);

  TextRes voidMethod();

  @Name("renamed_method")
  TextRes renamedMethod();

  @Name("renamed_method")
  TextRes renamedMethodTwo();

  TextRes methodWithArgument(String arg0);
  TextRes methodWithTwoArguments(String arg0, int arg1);

  @Default("the_default_value")
  TextRes methodWithDefaultValue();
}
