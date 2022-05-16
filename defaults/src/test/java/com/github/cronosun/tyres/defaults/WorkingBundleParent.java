package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.Res;

public interface WorkingBundleParent {
  Res<Msg> somethingFromParent();
  Res<Msg> somethingFromParentWithArgument(String argument);
}
