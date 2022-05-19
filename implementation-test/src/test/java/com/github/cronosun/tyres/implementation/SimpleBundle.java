package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;

public interface SimpleBundle {
  SimpleBundle INSTANCE = TyRes.create(SimpleBundle.class);

  MsgRes voidMethod();

  @Name("renamed_method")
  MsgRes renamedMethod();

  @Name("renamed_method")
  MsgRes renamedMethodTwo();

  MsgRes methodWithArgument(String arg0);
  MsgRes methodWithTwoArguments(String arg0, int arg1);

  @Default("the_default_value")
  MsgRes methodWithDefaultValue();
}
