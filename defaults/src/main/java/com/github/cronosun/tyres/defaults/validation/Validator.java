package com.github.cronosun.tyres.defaults.validation;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.defaults.backends.BinBackend;
import com.github.cronosun.tyres.defaults.backends.MsgStrBackend;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface Validator {
  static Validator newDefaultValidator(MsgStrBackend msgStrBackend, BinBackend binBackend) {
    return new DefaultValidator(msgStrBackend, binBackend);
  }

  ValidationErrors validationErrors(Object bundle, Set<Locale> locales);

  @Nullable
  default String validate(Object bundle, Set<Locale> locales) {
    var errors = validationErrors(bundle, locales);
    if (errors.errors().isEmpty()) {
      return null;
    } else {
      return errors.conciseDebugString();
    }
  }
}
