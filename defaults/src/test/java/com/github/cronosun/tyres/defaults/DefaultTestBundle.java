package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

public interface DefaultTestBundle {
  DefaultTestBundle INSTANCE = TyRes.create(DefaultTestBundle.class);

  @Default("This is the message ''{0}''.")
  MsgRes withConfiguredDefault(String arg);

  @Default("No, this default is not taken, since it's also in the properties.")
  MsgRes somethingThatIsAlsoFoundInProperty();
}
