package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.implementation.backends.BinBackend;
import com.github.cronosun.tyres.implementation.backends.MsgStrBackend;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultValidator implements Validator {

  private final MsgStrBackend msgStrBackend;
  private final BinBackend binBackend;

  DefaultValidator(MsgStrBackend msgStrBackend, BinBackend binBackend) {
    this.msgStrBackend = msgStrBackend;
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
    var resourceNamesInBundle = msgStrBackend.resourceNamesInBundleForValidation(
      bundleInfo,
      locale
    );
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
      .filter(resource -> resource.info() instanceof ResInfo.Str)
      .map(resource -> ((ResInfo.Str) resource.info()).name())
      .collect(Collectors.toUnmodifiableSet());
  }

  @Nullable
  private ValidationError validateDeclaredResourceIsOk(Res<?> res, Locale locale) {
    var resInfo = res.info();
    var optional = isOptional(res);
    if (resInfo instanceof ResInfo.Str) {
      var strResInfo = (ResInfo.Str) resInfo;
      if (res instanceof MsgRes) {
        return validateMsg(strResInfo, locale, optional);
      } else {
        if (!optional) {
          return validateStrExists(strResInfo, locale);
        } else {
          return null;
        }
      }
    } else if (resInfo instanceof ResInfo.Bin) {
      var binResInfo = (ResInfo.Bin) resInfo;
      if (!optional) {
        return validateBinExists(binResInfo, locale);
      } else {
        return null;
      }
    } else {
      throw new TyResException("Unknown resource kind: " + resInfo);
    }
  }

  @Nullable
  private ValidationError validateStrExists(ResInfo.Str resInfo, Locale locale) {
    if (!msgStrBackend.validateStringExists(resInfo, locale)) {
      return new ValidationError.ResourceNotFound(resInfo, locale);
    } else {
      return null;
    }
  }

  @Nullable
  private ValidationError validateBinExists(ResInfo.Bin resInfo, Locale locale) {
    if (!binBackend.validateExists(resInfo, locale)) {
      return new ValidationError.ResourceNotFound(resInfo, locale);
    } else {
      return null;
    }
  }

  @Nullable
  private ValidationError validateMsg(ResInfo.Str resInfo, Locale locale, boolean optional) {
    // number of arguments?
    var method = resInfo.method();
    var numberOfArguments = method.getParameterCount();
    return msgStrBackend.validateMessage(resInfo, numberOfArguments, locale, optional);
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
