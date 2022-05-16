package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.TyRes;

public interface TestBundle {
  TestBundle INSTANCE = TyRes.create(TestBundle.class);

  Res<Msg> sayHello();
}
