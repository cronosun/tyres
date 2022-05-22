package com.github.cronosun.tyres.defaults.bin;

import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.TyRes;

public interface BinTestBundle {
  BinTestBundle INSTANCE = TyRes.create(BinTestBundle.class);

  @File("no_localization.txt")
  Resources.BinRes resourceNoLocalization();

  @File("localized.txt")
  Resources.BinRes resourceLocalized();

  @File("only_german.txt")
  Resources.BinRes onlyGerman();
}
