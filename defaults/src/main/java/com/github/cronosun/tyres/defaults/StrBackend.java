package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 * Backend for string-like sources: plain strings and messages.
 */
@ThreadSafe
public interface StrBackend {
  /**
   * Returns the default implementation that uses {@link java.util.ResourceBundle}.
   */
  static StrBackend usingResourceBundle() {
    return DefaultStrBackend.instance();
  }

  /**
   * Returns the formatted message (if found).
   * <p>
   * Does not throw if the resource cannot be found - but expect the implementaton to throw @{@link TyResException} if
   * something else is wrong, like invalid arguments or an invalid message (a message that cannot be parsed).
   *
   * Note: Throws resource is not {@link ResInfoDetails.Kind#STRING}.
   *
   * @param args The arguments (never null; but can be empty): The arguments are already resolved,
   *             implementations MUST NOT try to resolve them.
   */
  @Nullable
  String maybeMessage(ResInfo resInfo, Object[] args, Locale locale);

  /**
   * Validates the given message.
   * <p>
   * Note: This is an optional operation. Implementations are allowed to just return <code>null</code> here
   * (this disables this validation).
   *
   * Note: Throws resource is not {@link ResInfoDetails.Kind#STRING}.
   *
   * @param optional If this is true, validation does not fail if the resource does not exist. But if the resource
   *                 exists, it's validated.
   **/
  @Nullable
  default ValidationError validateMessage(
    ResInfo resInfo,
    int numberOfArguments,
    Locale locale,
    boolean optional
  ) {
    var pattern = maybeString(resInfo, locale);
    if (pattern != null) {
      MessageFormat msgFormat;
      try {
        msgFormat = new MessageFormat(pattern, locale);
      } catch (IllegalArgumentException invalidPattern) {
        return new ValidationError.InvalidMsgPattern(resInfo, locale, pattern);
      }
      var expectedNumberOfArguments = msgFormat.getFormats().length;
      if (numberOfArguments != expectedNumberOfArguments) {
        return new ValidationError.InvalidNumberOfArguments(
          resInfo,
          locale,
          pattern,
          numberOfArguments,
          expectedNumberOfArguments
        );
      } else {
        return null;
      }
    } else {
      if (!optional) {
        return new ValidationError.ResourceNotFound(resInfo, locale);
      } else {
        // no problem, resource is optional.
        return null;
      }
    }
  }

  /**
   * Returns the plain string (not formatted) - if found. If there's no such resource, returns <code>null</code>.
   * <p>
   * Does not throw if the resource cannot be found.
   * <p>
   * Note: This must also return the pattern for messages: Plain strings and messages must use the same backend.
   * Calling this method with a {@link ResInfo} intended for {@link #maybeMessage(ResInfo, Object[], Locale)} must
   * return the message pattern.
   *
   * Note: Throws resource is not {@link ResInfoDetails.Kind#STRING}.
   */
  @Nullable
  String maybeString(ResInfo resInfo, Locale locale);

  /**
   * Returns <code>true</code> if the given string exists.
   */
  default boolean validateStringExists(ResInfo resInfo, Locale locale) {
    return maybeString(resInfo, locale) != null;
  }

  /**
   * Returns all names (see {@link ResInfoDetails.StrResource#name()}) that would produce a result when used
   * with {@link #maybeString(ResInfo, Locale)}.
   * <p>
   * Note 1: This method is optional. If the implementation does not support this operation, return <code>null</code>.
   * Note 2: Expect the returned set to be immutable.
   * Note 3: This method should only be used for validation (performance).
   */
  @Nullable
  Set<String> resourceNamesInBundleForValidation(BundleInfo bundleInfo, Locale locale);
}
