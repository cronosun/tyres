package com.github.cronosun.tyres.defaults.delegate;

import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.core.TyRes;

public interface ExtendedResourcesBundle {
  ExtendedResourcesBundle INSTANCE = TyRes.create(ExtendedResourcesBundle.class);
  StrRes sayHello();
  StrRes holidayLeaveDaysPerYear();
}
