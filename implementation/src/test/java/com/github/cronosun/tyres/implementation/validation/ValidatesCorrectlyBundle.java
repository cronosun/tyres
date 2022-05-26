package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.core.experiment.Bin;
import com.github.cronosun.tyres.core.experiment.Fmt;
import com.github.cronosun.tyres.core.experiment.Text;

public interface ValidatesCorrectlyBundle {
  Fmt present();

  Text presentToo();

  Fmt msgWithArgument(String value);

  @File("some_text.txt")
  Bin someText();
}
