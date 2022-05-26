package com.github.cronosun.tyres.implementation.localized_msg;

import com.github.cronosun.tyres.core.experiment.Fmt;
import com.github.cronosun.tyres.core.experiment.Text;

public interface LocalizedMsgBundle {
  Text colour();

  Fmt sayHello(String toWhom);

  Fmt messageNotPresentForSomeLocales();
}
