package com.github.cronosun.tyres.defaults.validation;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

public interface MissingMessageForGermanBundle {
  MissingMessageForGermanBundle INSTANCE = TyRes.create(MissingMessageForGermanBundle.class);

  MsgRes missingForGerman(String argument);
}
