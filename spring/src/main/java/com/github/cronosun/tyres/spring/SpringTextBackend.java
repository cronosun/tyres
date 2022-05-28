package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.implementation.MessageFormatter;
import com.github.cronosun.tyres.implementation.TextBackend;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

  public SpringTextBackend(MessageSourceProvider messageSourceProvider, MessageFormatter messageFormatter) {
    this.messageSourceProvider = messageSourceProvider;
    this.messageFormatter = messageFormatter;
  }

  @Override
  public @Nullable String maybeFmt(ResInfo.TextResInfo info, Object[] args, Locale locale) {
    var baseName = info.bundleInfo().effectiveBaseName();
    var source = messageSourceProvider.messageSource(baseName, locale);
    var name = info.effectiveName();
    try {
      var formattedTextFromMessageSource = source.message(name, args, locale);
      if (formattedTextFromMessageSource!=null) {
        return formattedTextFromMessageSource;
      }
    } catch (Exception exception) {
      var fmtArgs = Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", "));
      throw new TyResException("Unable to format '" + info.conciseDebugString() + "' with arguments '" + fmtArgs + "'.", exception);
    }

    // ok, nothing found. try the default value.
    var patternFromDefaultValue = info.defaultValue();
    if (patternFromDefaultValue!=null) {
      return messageFormatter.format(patternFromDefaultValue, args, locale);
    }

    // nothing found.
    return null;
  }

  @Override
  public void validateFmt(ResInfo.TextResInfo info, Locale locale) {
    var baseName = info.bundleInfo().effectiveBaseName();
    var source = messageSourceProvider.messageSource(baseName, locale);
    var name = info.effectiveName();
    var pattern = source.string(name, locale);
    if (pattern!=null) {
      // it's here, it must be valid
      var numberOfArguments = info.method().getParameterCount();
      this.messageFormatter.validatePattern(pattern, locale, numberOfArguments);
    } else {
      if (!info.validationOptional()) {
        throw new TyResException(
                "Text (fmt) " +
                        info.conciseDebugString() +
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
  public @Nullable String maybeText(ResInfo.TextResInfo info, Locale locale) {
    var baseName = info.bundleInfo().effectiveBaseName();
    var source = messageSourceProvider.messageSource(baseName, locale);
    var name = info.effectiveName();
    var textFromMessageSource = source.string(name, locale);
    if (textFromMessageSource!=null) {
      return textFromMessageSource;
    } else {
      // try the default value
      return info.defaultValue();
    }
  }

  @Override
  public void validateText(ResInfo.TextResInfo info, Locale locale) {
    var text = maybeText(info, locale);
    if (text == null && !info.validationOptional()) {
      throw new TyResException(
              "Text " +
                      info.conciseDebugString() +
                      " for locale '" +
                      locale.toLanguageTag() +
                      "' not found and it's not marked as optional (see @" +
                      Validation.class.getSimpleName() +
                      "' annotation)."
      );
    }
  }

  @Override
  public void validateNoSuperfluousResources(Stream<ResInfo.TextResInfo> allTextResourcesFromBundle, Locale locale) {
    var iterator = allTextResourcesFromBundle.iterator();
    BaseName baseName = null;
    BaseName originalBasename = null;
    var usedKeys = new HashSet<String>();
    while (iterator.hasNext()) {
      var item = iterator.next();
      var bundle = item.bundleInfo();

      // note: if base name != effective base name, we can't perform the validation, since multiple bundles
      // might use the same bundle.
      if (!bundle.effectiveBaseName().equals(bundle.baseName())) {
        return;
      }

      if (baseName == null) {
        baseName = bundle.effectiveBaseName();
        originalBasename = bundle.baseName();
      } else {
        if (!baseName.equals(bundle.effectiveBaseName())) {
          var baseNames = WithConciseDebugString.build(
                  List.of(baseName, bundle.effectiveBaseName())
          );
          throw new TyResException(
                  "Unable to validate, there are resources in the set with different base names: '" +
                          baseNames +
                          "'."
          );
        }
      }
      usedKeys.add(item.effectiveName());
    }

    if (baseName != null) {
      var source = messageSourceProvider.messageSource(baseName, locale);
      var allKeysFromBundle = source.resourceNamesInBundleForValidation(baseName, locale);
      if (allKeysFromBundle!=null) {
        for (var keyFromBundle : allKeysFromBundle) {
          if (!usedKeys.contains(keyFromBundle)) {
            throw new TyResException(
                    "The key '" +
                            keyFromBundle +
                            "' in bundle " +
                            baseName.conciseDebugString() +
                            " (locale '" +
                            locale.toLanguageTag() +
                            "') is not in use in the bunde (original base name " +
                            originalBasename.conciseDebugString() +
                            "). Remove it, or use it!"
            );
          }
        }
      }
    }
  }
}
