package com.github.cronosun.tyres.defaults.bin;

import com.github.cronosun.tyres.core.BinRes;
import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.TyRes;

public interface BinTestBundle {
  BinTestBundle INSTANCE = TyRes.create(BinTestBundle.class);

  @File("no_localization.txt")
  BinRes resourceNoLocalization();

  @File("localized.txt")
  BinRes resourceLocalized();

  @File("only_german.txt")
  BinRes onlyGerman();
}
