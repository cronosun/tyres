package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Date;

public interface WorkingBundle extends WorkingBundleParent {
  WorkingBundle INSTANCE = TyRes.create(WorkingBundle.class);

  MsgRes somethingThatCannotBeFound(String argument);

  MsgRes saySomethingAboutDaysOfTheWeek(Msg firstDay, Msg secondDay, Date date);

  MsgRes monday();

  MsgRes friday();

  MsgRes somethingThatIsMissing();

  MsgRes somethingWithUmlauts();

  MsgRes colour();

  MsgRes wrapLocalizedMessage(LocalizedMsg localizedMsg);
}
