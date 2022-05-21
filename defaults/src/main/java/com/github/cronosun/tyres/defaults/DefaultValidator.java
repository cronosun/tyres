package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultValidator implements Validator {

  private final StrBackend strBackend;
  private final BinBackend binBackend;

  DefaultValidator(StrBackend strBackend, BinBackend binBackend) {
    this.strBackend = strBackend;
    this.binBackend = binBackend;
  }

  @Override
  public ValidationErrors validationErrors(Object bundle, Set<Locale> locales) {
    var implementation = TyRes.implementation();
    var reflectionInfo = implementation.reflectionInfo(bundle);
    var builder = ValidationErrors.builder();
    for (var locale : locales) {
      collectValidationErrors(builder, reflectionInfo, locale);
    }
    return builder.build();
  }

  public void collectValidationErrors(
    ValidationErrors.Builder validationErrorsBuilder,
    ReflectionInfo reflectionInfo,
    Locale locale
  ) {
    // make sure everything that has been declared in this bundle is OK.
    for (var resource : reflectionInfo.resources()) {
      validationErrorsBuilder.addIfNotNull(validateDeclaredResourceIsOk(resource, locale));
    }

    // now make sure there's nothing superfluous
    var bundleInfo = reflectionInfo.bundleInfo();
    var resourceNamesInBundle = strBackend.resourceNamesInBundleForValidation(bundleInfo, locale);
    if (resourceNamesInBundle != null) {
      var declaredNames = collectDeclaredStringResourceNames(reflectionInfo);
      for (var resourceName : resourceNamesInBundle) {
        if (!declaredNames.contains(resourceName)) {
          validationErrorsBuilder.add(
            new ValidationError.SuperfluousResource(bundleInfo, locale, resourceName)
          );
        }
      }
    }
  }

  private Set<String> collectDeclaredStringResourceNames(ReflectionInfo reflectionInfo) {
    return reflectionInfo
      .resources()
      .stream()
      .filter(resource -> resource.info().details().kind() == ResInfoDetails.Kind.STRING)
      .map(resource -> resource.info().details().asStringResource().name())
      .collect(Collectors.toUnmodifiableSet());
  }

  @Nullable
  private ValidationError validateDeclaredResourceIsOk(Res<?> res, Locale locale) {
    var resInfo = res.info();
    var kind = resInfo.details().kind();
    var optional = isOptional(res);
    switch (kind) {
      case STRING:
        if (res instanceof MsgRes) {
          return validateMsg(resInfo, locale, optional);
        } else {
          if (!optional) {
            return validateStrExists(resInfo, locale);
          } else {
            return null;
          }
        }
      case BINARY:
        if (!optional) {
          return validateBinExists(resInfo, locale);
        } else {
          return null;
        }
      default:
        throw new TyResException("Unknown resource kind: " + kind);
    }
  }

  @Nullable
  private ValidationError validateStrExists(ResInfo resInfo, Locale locale) {
    if (!strBackend.validateStringExists(resInfo, locale)) {
      return new ValidationError.ResourceNotFound(resInfo, locale);
    } else {
      return null;
    }
  }

  @Nullable
  private ValidationError validateBinExists(ResInfo resInfo, Locale locale) {
    if (!binBackend.validateExists(resInfo, locale)) {
      return new ValidationError.ResourceNotFound(resInfo, locale);
    } else {
      return null;
    }
  }

  @Nullable
  private ValidationError validateMsg(ResInfo resInfo, Locale locale, boolean optional) {
    // number of arguments?
    var method = resInfo.method();
    var numberOfArguments = method.getParameterCount();
    return strBackend.validateMessage(resInfo, numberOfArguments, locale, optional);
  }

  private boolean isOptional(Res<?> res) {
    var method = res.info().method();
    var validationAnnotation = method.getAnnotation(Validation.class);
    if (validationAnnotation != null) {
      return validationAnnotation.optional();
    } else {
      return false;
    }
  }
}