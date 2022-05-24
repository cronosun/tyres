package com.github.cronosun.tyres.defaults.resolvable_list;

import com.github.cronosun.tyres.core.ResolvableListConfiguration;
import com.github.cronosun.tyres.core.TyRes;

public interface CustomResolvableListConfiguration extends ResolvableListConfiguration {
  CustomResolvableListConfiguration INSTANCE = TyRes.create(
    CustomResolvableListConfiguration.class
  );
}
