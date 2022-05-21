package com.github.cronosun.tyres.defaults.validation;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.defaults.Implementation;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationTest {

  @Test
  void validatedWithoutErrors() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    resources.validate(
      ValidatesCorrectlyBundle.INSTANCE,
      Set.of(Locale.GERMANY, Locale.ENGLISH, Locale.GERMAN, Locale.US, Locale.CANADA)
    );
  }

  @Test
  void validateThrowsSinceThereIsNoItalianAvailable() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    Assertions.assertThrows(
      TyResException.class,
      () -> resources.validate(ValidatesCorrectlyBundle.INSTANCE, Set.of(Locale.ITALIAN))
    );
  }

  @Test
  void validationDetectsMissingFileForLocale() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // english is ok (there's a file for that).
    resources.validate(FileMissingForGermanBundle.INSTANCE, Set.of(Locale.ENGLISH, Locale.US));
    // german should throw (there's no german file).
    Assertions.assertThrows(
      TyResException.class,
      () ->
        resources.validate(
          FileMissingForGermanBundle.INSTANCE,
          Set.of(Locale.ENGLISH, Locale.US, Locale.GERMAN)
        )
    );
  }

  @Test
  void validationDetectsMissingMessageForLocale() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // english is ok (there's a message in the .properties file).
    resources.validate(MissingMessageForGermanBundle.INSTANCE, Set.of(Locale.ENGLISH, Locale.US));
    // german should throw (there's a missing resource in the .properties file).
    Assertions.assertThrows(
      TyResException.class,
      () ->
        resources.validate(
          MissingMessageForGermanBundle.INSTANCE,
          Set.of(Locale.ENGLISH, Locale.US, Locale.GERMAN)
        )
    );
  }

  @Test
  void validationDetectsInvalidPattern() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // will take the ROOT locale, this locale is ok.
    resources.validate(InvalidPatternBundle.INSTANCE, Set.of(Locale.ITALIAN));
    // But in the english translation, there's a problem.
    Assertions.assertThrows(
      TyResException.class,
      () -> resources.validate(InvalidPatternBundle.INSTANCE, Set.of(Locale.US))
    );
    Assertions.assertThrows(
      TyResException.class,
      () -> resources.validate(InvalidPatternBundle.INSTANCE, Set.of(Locale.ENGLISH))
    );
  }

  @Test
  void validationRespectsTheDefaultValue() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // Validator detects the Default annotation and is happy.
    resources.validate(
      DefaultValueIsConsideredAsPresentBundle.INSTANCE,
      Set.of(Locale.ITALIAN, Locale.GERMAN, Locale.US)
    );
  }

  @Test
  void validatorDetectsErrorsInPatternsInTheDefaultAnnotation() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // There's a problem with the pattern or the method (wrong number of arguments).
    Assertions.assertThrows(
      TyResException.class,
      () ->
        resources.validate(
          ValidatorAlsoDetectsErrorsInDefaultValues.INSTANCE,
          Set.of(Locale.ITALIAN)
        )
    );
  }

  @Test
  void validatorDetectsSuperfluousResources() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // See the .properties file, there's 'butThisIsSomethingThatIsNoLongerInUse' (that's not referenced in the interface).
    Assertions.assertThrows(
      TyResException.class,
      () -> resources.validate(SuperfluousResourceBundle.INSTANCE, Set.of(Locale.ITALIAN))
    );
  }

  @Test
  void validatorRespectsTheOptionalPropertyButIfItIsPresentHasToBeValid() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // german and english validate: english has a correct message pattern; german has no such translation (which is
    // ok, see the @Validation annotation).
    resources.validate(OptionalResourcesBundle.INSTANCE, Set.of(Locale.ENGLISH, Locale.GERMAN));
    // but italy errors: If something is optional, it does not mean that it can be invalid (just means it can be absent).
    Assertions.assertThrows(
      TyResException.class,
      () -> resources.validate(OptionalResourcesBundle.INSTANCE, Set.of(Locale.ITALIAN))
    );
  }
}
