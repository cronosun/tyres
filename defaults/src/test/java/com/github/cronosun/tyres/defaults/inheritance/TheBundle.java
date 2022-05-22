package com.github.cronosun.tyres.defaults.inheritance;

import com.github.cronosun.tyres.core.*;

public interface TheBundle extends ParentBundle {
  TheBundle INSTANCE = TyRes.create(TheBundle.class);

  MsgRes anotherMessage();

  StrRes anotherString();

  /**
   * The {@link Rename} annotation in {@link ParentBundle} has no effect, since method is declared again.
   */
  StrRes anotherThingFromParent();

  @Default("this is the default value")
  StrRes somethingWithDefault();

  @Rename("thisIsTheCorrectName")
  StrRes somethingIsWrongWithThisName();

  @Rename("thisRenameHereIsEffective")
  StrRes stringWithOverwrittenRenameInSubInterface();

  @Default("Default value from sub-interface")
  StrRes withDefaultAnnotation2();
}
