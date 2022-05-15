package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.TyRes;

public interface CustomMsgListConfiguration extends MsgListConfiguration {
  CustomMsgListConfiguration INSTANCE = TyRes.create(CustomMsgListConfiguration.class);
}
