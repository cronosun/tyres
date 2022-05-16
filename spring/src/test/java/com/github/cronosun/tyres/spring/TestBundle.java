package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

public interface TestBundle {
  TestBundle INSTANCE = TyRes.create(TestBundle.class);

  MsgRes sayHello();
}
