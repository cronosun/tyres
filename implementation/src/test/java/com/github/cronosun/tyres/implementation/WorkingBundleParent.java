package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.MsgRes;

public interface WorkingBundleParent {
  MsgRes somethingFromParent();

  MsgRes somethingFromParentWithArgument(String argument);
}
