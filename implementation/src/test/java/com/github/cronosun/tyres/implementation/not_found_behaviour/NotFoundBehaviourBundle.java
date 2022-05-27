package com.github.cronosun.tyres.implementation.not_found_behaviour;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface NotFoundBehaviourBundle {
  Text textNotFound();

  @File("not_found.txt")
  Bin binNotFound();

  Fmt fmtNotFound(String item);
}
