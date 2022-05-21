package com.github.cronosun.tyres.defaults.validation;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;
import com.github.cronosun.tyres.core.Validation;

public interface OptionalResourcesBundle {
  OptionalResourcesBundle INSTANCE = TyRes.create(OptionalResourcesBundle.class);

  @Validation(optional = true)
  MsgRes thisMessageIsOptional(String name);
}
