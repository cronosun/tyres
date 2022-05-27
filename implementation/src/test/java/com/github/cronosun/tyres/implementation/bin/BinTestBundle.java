package com.github.cronosun.tyres.implementation.bin;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.File;

public interface BinTestBundle {
  @File("no_localization.txt")
  Bin resourceNoLocalization();

  @File("localized.txt")
  Bin resourceLocalized();

  @File("only_german.txt")
  Bin onlyGerman();
}
