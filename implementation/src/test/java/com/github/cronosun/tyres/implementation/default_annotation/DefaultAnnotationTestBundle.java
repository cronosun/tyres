package com.github.cronosun.tyres.implementation.default_annotation;

import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.experiment.Fmt;
import com.github.cronosun.tyres.core.experiment.Text;

public interface DefaultAnnotationTestBundle {
  @Default("This is the message ''{0}''.")
  Fmt withConfiguredDefault(String arg);

  @Default("No, this default is not taken, since it's also in the properties.")
  Fmt somethingThatIsAlsoFoundInProperty();

  @Default("Yes, this is the string to use")
  Text stringResWithConfiguredDefault();

  @Default("No, this value is not used")
  Text stringResourceThatIsAlsoFoundInProperty();
}
