package com.github.cronosun.tyres.defaults.msg_list;

import com.github.cronosun.tyres.core.TyRes;
import com.github.cronosun.tyres.defaults.MsgListConfiguration;

public interface CustomMsgListConfiguration extends MsgListConfiguration {
  CustomMsgListConfiguration INSTANCE = TyRes.create(CustomMsgListConfiguration.class);
}
