package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.Rename;
import com.github.cronosun.tyres.core.TyRes;

public interface SimpleBundle {
  SimpleBundle INSTANCE = TyRes.create(SimpleBundle.class);

  MsgRes voidMethod();

  @Rename("renamedMethod")
  MsgRes renamedMethod();

  @Rename("renamedMethod")
  MsgRes renamedMethodTwo();

  MsgRes methodWithArgument(String arg0);

  MsgRes methodWithTwoArguments(String arg0, int arg1);

  @Default("the_default_value")
  MsgRes methodWithDefaultValue();
}
