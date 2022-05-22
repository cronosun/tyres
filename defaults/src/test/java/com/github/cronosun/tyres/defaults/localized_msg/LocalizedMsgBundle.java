package com.github.cronosun.tyres.defaults.localized_msg;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.core.TyRes;

public interface LocalizedMsgBundle {
  LocalizedMsgBundle INSTANCE = TyRes.create(LocalizedMsgBundle.class);

  StrRes colour();
  MsgRes sayHello(String toWhom);
  MsgRes messageNotPresentForSomeLocales();
}
