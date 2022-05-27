package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface ValidatesCorrectlyBundle {
  Fmt present();

  Text presentToo();

  Fmt msgWithArgument(String value);

  @File("some_text.txt")
  Bin someText();
}
