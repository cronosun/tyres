package com.github.cronosun.tyres.defaults.inheritance;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.Rename;
import com.github.cronosun.tyres.core.StrRes;

public interface ParentBundle {
  MsgRes somethingFromParent(String argument);

  /**
   * Rename is ignored, since {@link TheBundle} declares the method again without {@link Rename}-annotation.
   */
  @Rename("rename_is_ignored_since_method_is_declared_in_sub_interface_again")
  StrRes anotherThingFromParent();

  /**
   * This renaming is effective.
   */
  @Rename("newNameDueToRenameAnnotationInParent")
  StrRes thisIsRenamedInParent();

  /**
   * This renaming is ignored, since {@link TheBundle} declares the method again with a different
   * {@link Rename} annotation.
   */
  @Rename("this_is_ignored")
  StrRes stringWithOverwrittenRenameInSubInterface();

  @Default("Default value from parent 1")
  StrRes withDefaultAnnotation1();

  @Default("Default value from parent 2")
  StrRes withDefaultAnnotation2();
}
