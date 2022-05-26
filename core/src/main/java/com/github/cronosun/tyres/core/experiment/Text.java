package com.github.cronosun.tyres.core.experiment;

import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

/**
 * A resource that produces text. A specialization is {@link Fmt} (formatted and with 0-n arguments).
 */
public interface Text extends Resolvable {

  @Nullable
  String getText(@Nullable Locale locale, NotFoundConfig.WithNullAndDefault notFoundConfig);

  default String get(Locale locale, NotFoundConfig notFoundConfig) {
    var localeNonNull = Objects.requireNonNull(locale, "Locale is missing");
    var notFoundConfigNotNull = Objects.requireNonNull(notFoundConfig, "Not-found config");
    var text = getText(localeNonNull, notFoundConfigNotNull.withNullAndDefault());
    return Objects.requireNonNull(text, "Given text returned by the implementation is null (this violates the contract).");
  }

  default String get(Locale locale) {
    return get(locale, NotFoundConfig.DEFAULT);
  }

  default String get() {
    var text = getText(null, NotFoundConfig.WithNullAndDefault.DEFAULT);
    return Objects.requireNonNull(text, "Given text returned by the implementation is null (this violates the contract).");
  }

  default String get(NotFoundConfig notFoundConfig) {
    var text = getText(null, notFoundConfig.withNullAndDefault());
    return Objects.requireNonNull(text, "Given text returned by the implementation is null (this violates the contract).");
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
  default Text get(Resources2 resources) {
    return this;
  }
}
