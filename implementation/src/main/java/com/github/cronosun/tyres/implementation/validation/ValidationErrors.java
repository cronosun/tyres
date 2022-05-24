package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.WithConciseDebugString;
import java.util.*;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public final class ValidationErrors implements WithConciseDebugString {

  private static final ValidationErrors EMPTY = new ValidationErrors(Set.of());
  private final Set<ValidationError> errors;

  private ValidationErrors(Set<ValidationError> errors) {
    this.errors = errors;
  }

  public static ValidationErrors empty() {
    return EMPTY;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Set<ValidationError> errors() {
    return errors;
  }

  public void throwIfHasErrors() {
    if (!errors().isEmpty()) {
      throw new TyResException(conciseDebugString());
    }
  }

  @Override
  public String toString() {
    return "ValidationErrors{" + errors + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ValidationErrors that = (ValidationErrors) o;
    return errors.equals(that.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errors);
  }

  @Override
  public String conciseDebugString() {
    if (errors.isEmpty()) {
      return WithConciseDebugString.build(List.of("no_validation_errors"));
    } else {
      return WithConciseDebugString.build(List.of("validation_errors", errors));
    }
  }

  public static final class Builder {

    @Nullable
    private Set<ValidationError> errors;

    private Builder() {}

    public void add(ValidationError error) {
      var errors = this.errors;
      if (errors == null) {
        errors = new HashSet<>();
        this.errors = errors;
      }
      errors.add(error);
    }

    public void addIfNotNull(@Nullable ValidationError maybeError) {
      if (maybeError != null) {
        add(maybeError);
      }
    }

    public ValidationErrors build() {
      var takenErrors = this.errors;
      this.errors = null;
      if (takenErrors != null) {
        return new ValidationErrors(Collections.unmodifiableSet(takenErrors));
      } else {
        return ValidationErrors.empty();
      }
    }
  }
}
