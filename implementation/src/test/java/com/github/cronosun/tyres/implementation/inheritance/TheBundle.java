package com.github.cronosun.tyres.implementation.inheritance;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface TheBundle extends ParentBundle {
  Fmt anotherMessage();

  Text anotherString();

  /**
   * The {@link Rename} annotation in {@link ParentBundle} has no effect, since method is declared again.
   */
  Text anotherThingFromParent();

  @Default("this is the default value")
  Text somethingWithDefault();

  @Rename("thisIsTheCorrectName")
  Text somethingIsWrongWithThisName();

  @Rename("thisRenameHereIsEffective")
  Text stringWithOverwrittenRenameInSubInterface();

  @Default("Default value from sub-interface")
  Text withDefaultAnnotation2();
}
