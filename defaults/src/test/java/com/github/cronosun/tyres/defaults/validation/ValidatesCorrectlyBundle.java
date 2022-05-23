package com.github.cronosun.tyres.defaults.validation;

import com.github.cronosun.tyres.core.*;

public interface ValidatesCorrectlyBundle {
  ValidatesCorrectlyBundle INSTANCE = TyRes.create(ValidatesCorrectlyBundle.class);

  MsgRes present();

  StrRes presentToo();

  MsgRes msgWithArgument(String value);

  @File("some_text.txt")
  BinRes someText();
}
