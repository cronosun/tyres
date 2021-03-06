package com.github.cronosun.tyres.core;

import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * A resource that produces text. A specialization is {@link Fmt} (formatted and with 0-n arguments).
 */
@ThreadSafe
public interface Text extends Resolvable {
  /**
   * Returns the {@link EntryInfo.TextEntry} that was used to construct this
   * text instance. It's not present if this {@link Text} instance was constructed from something else (such as
   * {@link Localized} for example).
   */
  @Nullable
  EntryInfo.TextEntry resInfo();

  @Nullable
  String getText(@Nullable Locale locale, NotFoundConfig.WithNullAndDefault notFoundConfig);

  default String get(Locale locale, NotFoundConfig notFoundConfig) {
    var localeNonNull = Objects.requireNonNull(locale, "Locale is missing");
    var notFoundConfigNotNull = Objects.requireNonNull(notFoundConfig, "Not-found config");
    var text = getText(localeNonNull, notFoundConfigNotNull.withNullAndDefault());
    return Objects.requireNonNull(
      text,
      "Given text returned by the implementation is null (this violates the contract)."
    );
  }

  default String get(Locale locale) {
    return get(locale, NotFoundConfig.DEFAULT);
  }

  default String get() {
    var text = getText(null, NotFoundConfig.WithNullAndDefault.DEFAULT);
    return Objects.requireNonNull(
      text,
      "Given text returned by the implementation is null (this violates the contract)."
    );
  }

  default String get(NotFoundConfig notFoundConfig) {
    var text = getText(null, notFoundConfig.withNullAndDefault());
    return Objects.requireNonNull(
      text,
      "Given text returned by the implementation is null (this violates the contract)."
    );
  }

  @Nullable
  default String maybe() {
    return getText(null, NotFoundConfig.WithNullAndDefault.NULL);
  }

  @Nullable
  default String maybe(Locale locale) {
    var localeNonNull = Objects.requireNonNull(locale, "Locale is missing");
    return getText(localeNonNull, NotFoundConfig.WithNullAndDefault.NULL);
  }

  @Override
  default Text resolve(Resources resources) {
    return this;
  }
}
