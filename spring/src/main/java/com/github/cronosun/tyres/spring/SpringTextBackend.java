package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.implementation.MessageFormatter;
import com.github.cronosun.tyres.implementation.TextBackend;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link TextBackend} using spring's {@link org.springframework.context.MessageSource}.
 */
public final class SpringTextBackend implements TextBackend {

  private final MessageSourceProvider messageSourceProvider;
  private final MessageFormatter messageFormatter;

  public SpringTextBackend(
    MessageSourceProvider messageSourceProvider,
    MessageFormatter messageFormatter
  ) {
    this.messageSourceProvider = messageSourceProvider;
    this.messageFormatter = messageFormatter;
  }

  @Override
  public @Nullable String maybeFmt(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Object[] args,
    Locale locale
  ) {
    var source = messageSourceProvider.messageSource(baseName, locale);
    try {
      var formattedTextFromMessageSource = source.message(name, args, locale);
      if (formattedTextFromMessageSource != null) {
        return formattedTextFromMessageSource;
      }
    } catch (Exception exception) {
      var fmtArgs = Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", "));
      throw new TyResException(
        "Unable to format '" + entry.conciseDebugString() + "' with arguments '" + fmtArgs + "'.",
        exception
      );
    }

    // ok, nothing found. try the default value.
    var patternFromDefaultValue = entry.defaultValue();
    if (patternFromDefaultValue != null) {
      return messageFormatter.format(patternFromDefaultValue, args, locale);
    }

    // nothing found.
    return null;
  }

  @Override
  public void validateFmt(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Locale locale
  ) {
    var source = messageSourceProvider.messageSource(baseName, locale);
    var pattern = source.string(name, locale);
    if (pattern != null) {
      // it's here, it must be valid
      var numberOfArguments = entry.method().getParameterCount();
      this.messageFormatter.validatePattern(pattern, locale, numberOfArguments);
    } else {
      if (entry.required()) {
        throw new TyResException(
          "Text (fmt) " +
          entry.conciseDebugString() +
          " for locale '" +
          locale.toLanguageTag() +
          "' not found and it's not marked as optional (see @" +
          Validation.class.getSimpleName() +
          "' annotation)."
        );
      }
    }
  }

  @Override
  public @Nullable String maybeText(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Locale locale
  ) {
    var source = messageSourceProvider.messageSource(baseName, locale);
    var textFromMessageSource = source.string(name, locale);
    if (textFromMessageSource != null) {
      return textFromMessageSource;
    } else {
      // try the default value
      return entry.defaultValue();
    }
  }

  @Override
  public void validateText(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Locale locale
  ) {
    var text = maybeText(entry, baseName, name, locale);
    if (text == null && entry.required()) {
      throw new TyResException(
        "Text " +
        entry.conciseDebugString() +
        " for locale '" +
        locale.toLanguageTag() +
        "' not found and it's not marked as optional (see @" +
        Validation.class.getSimpleName() +
        "' annotation)."
      );
    }
  }

  @Override
  public @Nullable Stream<String> maybeAllResourcesInBundle(BaseName baseName, Locale locale) {
    var source = messageSourceProvider.messageSource(baseName, locale);
    var allKeysFromBundle = source.resourceNamesInBundleForValidation(baseName, locale);
    if (allKeysFromBundle != null) {
      return allKeysFromBundle.stream();
    } else {
      return null;
    }
  }
}
