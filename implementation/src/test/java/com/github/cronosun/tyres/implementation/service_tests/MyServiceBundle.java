package com.github.cronosun.tyres.implementation.service_tests;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

public interface MyServiceBundle {
  MyServiceBundle INSTANCE = TyRes.create(MyServiceBundle.class);

  MsgRes amountIsMissing();

  MsgRes amountIsTooSmall(int givenAmount);

  MsgRes amountIsTooLarge(int givenAmount);
}
