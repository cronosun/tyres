package com.github.cronosun.tyres.implementation.localized_msg;

import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface LocalizedMsgBundle {
  Text colour();

  Fmt sayHello(String toWhom);

  Fmt messageNotPresentForSomeLocales();
}
