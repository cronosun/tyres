package com.github.cronosun.tyres.implementation.inheritance;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Rename;
import com.github.cronosun.tyres.core.Text;

public interface ParentBundle {
  Fmt somethingFromParent(String argument);

  /**
   * Rename is ignored, since {@link TheBundle} declares the method again without {@link Rename}-annotation.
   */
  @Rename("rename_is_ignored_since_method_is_declared_in_sub_interface_again")
  Text anotherThingFromParent();

  /**
   * This renaming is effective.
   */
  @Rename("newNameDueToRenameAnnotationInParent")
  Text thisIsRenamedInParent();

  /**
   * This renaming is ignored, since {@link TheBundle} declares the method again with a different
   * {@link Rename} annotation.
   */
  @Rename("this_is_ignored")
  Text stringWithOverwrittenRenameInSubInterface();

  @Default("Default value from parent 1")
  Text withDefaultAnnotation1();

  @Default("Default value from parent 2")
  Text withDefaultAnnotation2();
}
