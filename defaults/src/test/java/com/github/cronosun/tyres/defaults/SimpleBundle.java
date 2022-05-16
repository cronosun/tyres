package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;

public interface SimpleBundle {
  SimpleBundle INSTANCE = TyRes.create(SimpleBundle.class);

  Res<Msg> voidMethod();

  @Rename("renamedMethod")
  Res<Msg> renamedMethod();

  @Rename("renamedMethod")
  Res<Msg> renamedMethodTwo();

  Res<Msg> methodWithArgument(String arg0);
  Res<Msg> methodWithTwoArguments(String arg0, int arg1);

  @Default("the_default_value")
  Res<Msg> methodWithDefaultValue();
}
