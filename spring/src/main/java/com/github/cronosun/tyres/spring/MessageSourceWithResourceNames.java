package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;

public interface MessageSourceWithResourceNames extends MessageSource {
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
