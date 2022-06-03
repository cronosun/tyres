package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@ThreadSafe
public interface TextBackend {
  @Nullable
  String maybeFmt(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Object[] args,
    Locale locale
  );

  /**
   * Validates the format, throws {@link com.github.cronosun.tyres.core.TyResException}
   * on error.
   * <p>
   * Note: This is an optional operation. If the implementation does not support validation,
   * this is a no-op. If it's implemented, implementations should do this:
   *
   * <ul>
   *     <li>If it's not optional (see {@link com.github.cronosun.tyres.core.Validation}) asserts that the resource is present.</li>
   *    <li>Asserts that the pattern is valid and number of arguments in the pattern match the number of arguments in the method.</li>
   * </ul>
   */
  void validateFmt(EntryInfo.TextEntry entry, BaseName baseName, String name, Locale locale);

  @Nullable
  String maybeText(EntryInfo.TextEntry entry, BaseName baseName, String name, Locale locale);

  /**
   * Validates the text, throws {@link com.github.cronosun.tyres.core.TyResException} on error.
   * <p>
   * Note: This is an optional operation. If the implementation does not support validation,
   * this is a no-op. If it's implemented, implementations should do this:
   */
  void validateText(EntryInfo.TextEntry entry, BaseName baseName, String name, Locale locale);

  /**
   * Lists all resources in given bundle (for validation).
   * <p>
   * Note 1: This is used for bundle validation only (detect superfluous resources). It might be a slow operation.
   * Note 2: This operation is optional. If it's not supported, just return <code>null</code>.
   */
  @Nullable
  Stream<String> maybeAllResourcesInBundle(BaseName baseName, Locale locale);
}
