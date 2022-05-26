package com.github.cronosun.tyres.implementation.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ValidationTest {

  @Test
  void validatedWithoutErrors() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    resources.validate(ValidatesCorrectlyBundle.class, Set.of(Locale.GERMANY, Locale.ENGLISH, Locale.GERMAN, Locale.US, Locale.CANADA));
  }

  @Test
  void validateThrowsSinceThereIsNoItalianAvailable() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    assertThrows(TyResException.class, () -> {
      resources.validate(ValidatesCorrectlyBundle.class, Locale.ITALIAN);
    });
  }

  @Test
  void validationDetectsMissingFileForLocale() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    // english is ok (there's a file for that).
    resources.validate(FileMissingForGermanBundle.class, Set.of(Locale.ENGLISH, Locale.US));
    // german should throw (there's no german file).
    assertThrows(TyResException.class, ()-> resources.validate(FileMissingForGermanBundle.class, Locale.GERMAN));
  }

  @Test
  void validationDetectsMissingMessageForLocale() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    // english is ok (there's a message in the .properties file).
    resources.validate(MissingMessageForGermanBundle.class, Set.of(Locale.ENGLISH, Locale.US));
    // german should throw (there's a missing resource in the .properties file).
    assertThrows(TyResException.class, ()-> resources.validate(MissingMessageForGermanBundle.class, Locale.GERMAN));
  }

  @Test
  void validationDetectsInvalidPattern() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    // will take the ROOT locale, this locale is ok.
    resources.validate(InvalidPatternBundle.class, Locale.ITALIAN);
    // But in the english translation, there's a problem.
    assertThrows(TyResException.class, () -> resources.validate(InvalidPatternBundle.class, Locale.ENGLISH));
    assertThrows(TyResException.class, () -> resources.validate(InvalidPatternBundle.class, Locale.US));
  }

  @Test
  void validationRespectsTheDefaultValue() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    // Validator detects the Default annotation and is happy.
    resources.validate(DefaultValueIsConsideredAsPresentBundle.class, Set.of(Locale.ITALIAN, Locale.GERMAN, Locale.US));
  }

  @Test
  void validatorDetectsErrorsInPatternsInTheDefaultAnnotation() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    // There's a problem with the pattern or the method (wrong number of arguments).
    assertThrows(TyResException.class, () -> resources.validate(ValidatorAlsoDetectsErrorsInDefaultValues.class, Locale.ITALIAN));
  }

  @Test
  void validatorDetectsSuperfluousResources() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    // See the .properties file, there's 'butThisIsSomethingThatIsNoLongerInUse' (that's not referenced in the interface).
    assertThrows(TyResException.class, () -> resources.validate(SuperfluousResourceBundle.class, Locale.ITALIAN));
  }

  @Test
  void validatorRespectsTheOptionalPropertyButIfItIsPresentHasToBeValid() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    // german and english validate: english has a correct message pattern; german has no such translation (which is
    // ok, see the @Validation annotation).
    resources.validate(OptionalResourcesBundle.class, Set.of(Locale.ENGLISH, Locale.GERMAN));
    // but italy errors: If something is optional, it does not mean that it can be invalid (just means it can be absent).
    assertThrows(TyResException.class, () -> resources.validate(OptionalResourcesBundle.class, Locale.ITALIAN));
  }

  @Test
  void patternsAreOnlyValidatedForFormattedResoucesNotForText() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var locale = Locale.ENGLISH;
    var bundle = resources.get(InvalidPatternInStrResBundle.class);

    resources.validate(InvalidPatternInStrResBundle.class, locale);

    // can also get the messages
    assertEquals(
      "Too many arguments: {0} {1} {2}",
            bundle.invalidPattern1().get(locale)
    );
    assertEquals(
      "Completely invalid: {0,UNKNOWN} {1,_?} {2",
      bundle.invalidPattern2().get(locale)
    );
    assertEquals(
      "Is is invalid {0x,??}, {1}",
      bundle.invalidPattern3().get(locale)
    );
    assertEquals(
      "Is is invalid {0,,_ {1}",
            bundle.invalidPattern4().get(locale)
    );

    // also works using the resolver (Text is Resolvable too).
    assertEquals(
      "Too many arguments: {0} {1} {2}",
      resources.resolve(bundle.invalidPattern1()).get(locale)
    );
    assertEquals(
      "Completely invalid: {0,UNKNOWN} {1,_?} {2",
            resources.resolve(bundle.invalidPattern2()).get(locale)
    );
    assertEquals(
      "Is is invalid {0x,??}, {1}",
            resources.resolve(bundle.invalidPattern3()).get(locale)
    );
    assertEquals(
      "Is is invalid {0,,_ {1}",
            resources.resolve(bundle.invalidPattern4()).get(locale)
    );

    // ok, this does not validate (missing translations for GERMAN).
    assertThrows(TyResException.class, () -> resources.validate(InvalidPatternInStrResBundle.class, Locale.GERMAN));
  }
}
