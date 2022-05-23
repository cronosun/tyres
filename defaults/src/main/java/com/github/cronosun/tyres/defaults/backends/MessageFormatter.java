package com.github.cronosun.tyres.defaults.backends;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ResInfoDetails;
import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.defaults.validation.ValidationError;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface MessageFormatter {
  static MessageFormatter defaultImplementation() {
    return DefaultMessageFormatter.instance();
  }

  /**
   * Formats the given message.
   * <p>
   * Errors: Might throw {@link com.github.cronosun.tyres.core.TyResException} if the pattern is invalid or the
   * arguments are invalid (too many arguments, too few arguments or argument cannot be formatted).
   *
   * @param args Note: Arguments are already resolved here (implementation MUST NOT resolve them).
   */
  String format(String pattern, Object[] args, Locale locale);

  /**
   * Validates the given message.
   * <p>
   * Note: This is an optional operation. Implementations are allowed to just return <code>null</code> here
   * (this disables this validation).
   *
   * Note: Throws resource is not {@link ResInfoDetails.Kind#STRING}.
   **/
  @Nullable
  ValidationError validateMessage(
    ResInfo resInfo,
    String msgPattern,
    int numberOfArguments,
    Locale locale
  );
}
