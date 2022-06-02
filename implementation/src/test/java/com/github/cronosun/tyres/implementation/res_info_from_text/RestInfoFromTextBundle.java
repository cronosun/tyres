package com.github.cronosun.tyres.implementation.res_info_from_text;

import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface RestInfoFromTextBundle {
  Text message1();
  Fmt message2();
  Fmt message3(String argument);
}
