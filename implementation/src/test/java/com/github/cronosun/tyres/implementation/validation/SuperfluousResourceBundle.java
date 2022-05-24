package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.core.TyRes;

public interface SuperfluousResourceBundle {
  SuperfluousResourceBundle INSTANCE = TyRes.create(SuperfluousResourceBundle.class);

  StrRes yesThisIsStillInUse();
}
