package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;

public interface SimpleBundle {
  SimpleBundle INSTANCE = TyRes.create(SimpleBundle.class);

  MsgRes voidMethod();

  @RenameMethod("renamedMethod")
  MsgRes renamedMethod();

  @RenameMethod("renamedMethod")
  MsgRes renamedMethodTwo();

  MsgRes methodWithArgument(String arg0);
  MsgRes methodWithTwoArguments(String arg0, int arg1);

  @Default("the_default_value")
  MsgRes methodWithDefaultValue();
}
