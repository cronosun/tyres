package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface TextBackend {
  @Nullable
  String maybeFmt(ResInfo.TextResInfo info, Object[] args, Locale locale);

  /**
   * Validates the format, throws {@link com.github.cronosun.tyres.core.TyResException}
   * on error.
   *
   * Note: This is an optional operation. If the implementation does not support validation,
   * this is a no-op. If it's implemented, implementations should do this:
   *
   * <ul>
   *     <li>If it's not optional (see {@link com.github.cronosun.tyres.core.Validation}) asserts that the resource is present.</li>
   *    <li>Asserts that the pattern is valid and number of arguments in the pattern match the number of arguments in the method.</li>
   * </ul>
   */
  void validateFmt(ResInfo.TextResInfo info, Locale locale);

  @Nullable
  String maybeText(ResInfo.TextResInfo info, Locale locale);

  /**
   * Validates the text, throws {@link com.github.cronosun.tyres.core.TyResException} on error.
   *
   * Note: This is an optional operation. If the implementation does not support validation,
   * this is a no-op. If it's implemented, implementations should do this:
   */
  void validateText(ResInfo.TextResInfo info, Locale locale);

  /**
   * Asserts that there are no superfluous resources, throws {@link com.github.cronosun.tyres.core.TyResException}
   * if there are.
   *
   * Note 1: This is an optional operation (if this is not supported, the method is a no-op).
   * Note 2: You MUST deliver all text resources from a bundle.
   */
  void validateNoSuperfluousResources(
    Stream<ResInfo.TextResInfo> allTextResourcesFromBundle,
    Locale locale
  );
}
