package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;

/**
 * It's similar to {@link MessageSource}.
 */
public interface ExtMessageSource {
  /**
   * See {@link MessageSource#getMessage(String, Object[], String, Locale)} with:
   *
   *  - defaultMessage <code>null</code>.
   *  - and: Also messages without arguments MUST be parsed.
   *
   * @throws IllegalArgumentException if message cannot be formatted / pattern is invalid.
   */
  @Nullable
  String message(String code, Object[] args, Locale locale);

  /**
   * See {@link MessageSource#getMessage(String, Object[], String, Locale)} and
   * {@link #message(String, Object[], Locale)} without arguments and it is NOT
   * formatted.
   */
  @Nullable
  String string(String code, Locale locale);

  /**
   * Returns all names in this message source.
   * <p>
   * Note 1: This method is optional. If the implementation does not support this operation, return <code>null</code>.
   * Note 2: Expect the returned set to be immutable.
   * Note 3: This method should only be used for validation (performance).
   */
  @Nullable
  Set<String> resourceNamesInBundleForValidation(BundleInfo bundleInfo, Locale locale);
}
