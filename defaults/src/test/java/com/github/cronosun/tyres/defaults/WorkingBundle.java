package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.TyRes;
import java.util.Date;

public interface WorkingBundle extends WorkingBundleParent {
  WorkingBundle INSTANCE = TyRes.create(WorkingBundle.class);

  Res<Msg> somethingThatCannotBeFound(String argument);

  Res<Msg> saySomethingAboutDaysOfTheWeek(Msg firstDay, Msg secondDay, Date date);

  Res<Msg> monday();

  Res<Msg> friday();

  Res<Msg> somethingThatIsMissing();

  Res<Msg> somethingWithUmlauts();

  Res<Msg> colour();

  Res<Msg> wrapLocalizedMessage(LocalizedMsg localizedMsg);
}
