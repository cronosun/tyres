package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.experiment.ResInfo;
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
   * Note: This is an optional operation. If the implementaton does not support validation,
   * this is a no-op. If it's implemented, implementations should do this:
   *
   * <ul>
   *     <li>If it's not optional (see {@link com.github.cronosun.tyres.core.Validation}) asserts that the resouce is present.</li>
   *    <li>Asserts that the pattern is valid and number of arguments in the pattern match the number of arguments in the method.</li>
   * </ul>
   */
  void validateFmt(ResInfo.TextResInfo info, Locale locale);

  @Nullable
  String maybeText(ResInfo.TextResInfo info, Locale locale);

  /**
   * Validates the text, throws {@link com.github.cronosun.tyres.core.TyResException} on error.
   *
   * Note: This is an optional operation. If the implementaton does not support validation,
   * this is a no-op. If it's implemented, implementations should do this:
   */
  void validateText(ResInfo.TextResInfo info, Locale locale);

  /**
   * Asserts that there are no superflous resources, throws {@link com.github.cronosun.tyres.core.TyResException}
   * if there are.
   *
   * Note 1: This is an optional operation (if this is not supported, the method is a no-op).
   * Note 2: You MUST deliver all text resources from a bundle.
   */
  void validateNoSuperfuousResouces(
    Stream<ResInfo.TextResInfo> allTextResoucesFromBundle,
    Locale locale
  );
}
