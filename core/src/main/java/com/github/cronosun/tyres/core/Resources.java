package com.github.cronosun.tyres.core;

import java.util.Locale;
import java.util.Set;

@ThreadSafe
public interface Resources {
  MsgResources msg();

  StrResources str();

  BinResources bin();

  Resolver resolver();

  /**
   * Validates the given bundle. Throws {@link TyResException} if the bundle is not correct / has validation errors.
   * This method should only be used in unit-tests.
   * <p>
   * Implementation notes: The behaviour of this method is highly implementation specific. For production code,
   * this implementation might even be a no-op. If it's implemented, it should do about this:
   * <p>
   * - Assert that all declared resources are found for the given locales (except the resource is marked as
   * optional, see {@link Validation}).
   * - If the resource is a {@link MsgRes}, also asserts that the message pattern is valid and the number of
   * arguments match with the pattern.
   * - If the validator supports it, also asserts that there are no unused (superfluous) resources.
   */
  void validate(Object bundle, Set<Locale> locales);

  /**
   * What should be done if the resource cannot be found.
   *
   * Used for example in {@link MsgResources#get(MsgRes, Locale)}.
   */
  MsgNotFoundStrategy notFoundStrategy();

  /**
   * Generates the fallback message (note, this is not to be confused with the default message,
   * see {@link ResInfoDetails.StrResource#defaultValue()}).
   */
  String fallbackFor(ResInfo resInfo, Object[] args);
  // TODO: Extract those things to something like "ResourcesCommon" ...
  // TODO: in "ResourcesCommon" -> also add something like @Nullable currentLocale (can be null)
}
