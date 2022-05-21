package com.github.cronosun.tyres.defaults.default_annotation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.core.TyRes;

public interface DefaultAnnotationTestBundle {
  DefaultAnnotationTestBundle INSTANCE = TyRes.create(DefaultAnnotationTestBundle.class);

  @Default("This is the message ''{0}''.")
  MsgRes withConfiguredDefault(String arg);

  @Default("No, this default is not taken, since it's also in the properties.")
  MsgRes somethingThatIsAlsoFoundInProperty();

  @Default("Yes, this is the string to use")
  StrRes stringResWithConfiguredDefault();

  @Default("No, this value is not used")
  StrRes stringResourceThatIsAlsoFoundInProperty();
}
