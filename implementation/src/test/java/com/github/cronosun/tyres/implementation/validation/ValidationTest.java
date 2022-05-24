package com.github.cronosun.tyres.implementation.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.implementation.Implementation;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ValidationTest {

  @Test
  void validatedWithoutErrors() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    assertNull(
      resources
        .common()
        .validate(
          ValidatesCorrectlyBundle.INSTANCE,
          Set.of(Locale.GERMANY, Locale.ENGLISH, Locale.GERMAN, Locale.US, Locale.CANADA)
        )
    );
  }

  @Test
  void validateThrowsSinceThereIsNoItalianAvailable() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    assertNotNull(
      resources.common().validate(ValidatesCorrectlyBundle.INSTANCE, Set.of(Locale.ITALIAN))
    );
  }

  @Test
  void validationDetectsMissingFileForLocale() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // english is ok (there's a file for that).
    assertNull(
      resources
        .common()
        .validate(FileMissingForGermanBundle.INSTANCE, Set.of(Locale.ENGLISH, Locale.US))
    );
    // german should throw (there's no german file).
    assertNotNull(
      resources
        .common()
        .validate(
          FileMissingForGermanBundle.INSTANCE,
          Set.of(Locale.ENGLISH, Locale.US, Locale.GERMAN)
        )
    );
  }

  @Test
  void validationDetectsMissingMessageForLocale() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // english is ok (there's a message in the .properties file).
    assertNull(
      resources
        .common()
        .validate(MissingMessageForGermanBundle.INSTANCE, Set.of(Locale.ENGLISH, Locale.US))
    );
    // german should throw (there's a missing resource in the .properties file).
    assertNotNull(
      resources
        .common()
        .validate(
          MissingMessageForGermanBundle.INSTANCE,
          Set.of(Locale.ENGLISH, Locale.US, Locale.GERMAN)
        )
    );
  }

  @Test
  void validationDetectsInvalidPattern() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // will take the ROOT locale, this locale is ok.
    assertNull(resources.common().validate(InvalidPatternBundle.INSTANCE, Set.of(Locale.ITALIAN)));
    // But in the english translation, there's a problem.
    assertNotNull(resources.common().validate(InvalidPatternBundle.INSTANCE, Set.of(Locale.US)));
    assertNotNull(
      resources.common().validate(InvalidPatternBundle.INSTANCE, Set.of(Locale.ENGLISH))
    );
  }

  @Test
  void validationRespectsTheDefaultValue() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // Validator detects the Default annotation and is happy.
    assertNull(
      resources
        .common()
        .validate(
          DefaultValueIsConsideredAsPresentBundle.INSTANCE,
          Set.of(Locale.ITALIAN, Locale.GERMAN, Locale.US)
        )
    );
  }

  @Test
  void validatorDetectsErrorsInPatternsInTheDefaultAnnotation() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // There's a problem with the pattern or the method (wrong number of arguments).
    assertNotNull(
      resources
        .common()
        .validate(ValidatorAlsoDetectsErrorsInDefaultValues.INSTANCE, Set.of(Locale.ITALIAN))
    );
  }

  @Test
  void validatorDetectsSuperfluousResources() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // See the .properties file, there's 'butThisIsSomethingThatIsNoLongerInUse' (that's not referenced in the interface).
    assertNotNull(
      resources.common().validate(SuperfluousResourceBundle.INSTANCE, Set.of(Locale.ITALIAN))
    );
  }

  @Test
  void validatorRespectsTheOptionalPropertyButIfItIsPresentHasToBeValid() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    // german and english validate: english has a correct message pattern; german has no such translation (which is
    // ok, see the @Validation annotation).
    assertNull(
      resources
        .common()
        .validate(OptionalResourcesBundle.INSTANCE, Set.of(Locale.ENGLISH, Locale.GERMAN))
    );
    // but italy errors: If something is optional, it does not mean that it can be invalid (just means it can be absent).
    assertNotNull(
      resources.common().validate(OptionalResourcesBundle.INSTANCE, Set.of(Locale.ITALIAN))
    );
  }

  @Test
  void patternsAreOnlyValidatedForMsgResNotForStrRes() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);
    var locale = Locale.ENGLISH;
    assertNull(resources.common().validate(InvalidPatternInStrResBundle.INSTANCE, Set.of(locale)));

    // can also get the messages
    assertEquals(
      "Too many arguments: {0} {1} {2}",
      resources.str().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern1(), locale)
    );
    assertEquals(
      "Completely invalid: {0,UNKNOWN} {1,_?} {2",
      resources.str().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern2(), locale)
    );
    assertEquals(
      "Is is invalid {0x,??}, {1}",
      resources.str().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern3(), locale)
    );
    assertEquals(
      "Is is invalid {0,,_ {1}",
      resources.str().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern4(), locale)
    );

    // also works using the resolver (StrRes is Resolvable too).
    assertEquals(
      "Too many arguments: {0} {1} {2}",
      resources.resolver().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern1(), locale)
    );
    assertEquals(
      "Completely invalid: {0,UNKNOWN} {1,_?} {2",
      resources.resolver().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern2(), locale)
    );
    assertEquals(
      "Is is invalid {0x,??}, {1}",
      resources.resolver().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern3(), locale)
    );
    assertEquals(
      "Is is invalid {0,,_ {1}",
      resources.resolver().get(InvalidPatternInStrResBundle.INSTANCE.invalidPattern4(), locale)
    );

    // ok, this does not validate (missing translations for GERMAN).
    assertNotNull(
      resources.common().validate(InvalidPatternInStrResBundle.INSTANCE, Set.of(Locale.GERMAN))
    );
  }
}
