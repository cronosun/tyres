package com.github.cronosun.tyres.defaults.validation;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.TyResException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public final class ValidationErrors {

  private static final ValidationErrors EMPTY = new ValidationErrors(Set.of());

  public static ValidationErrors empty() {
    return EMPTY;
  }

  private final Set<ValidationError> errors;

  private ValidationErrors(Set<ValidationError> errors) {
    this.errors = errors;
  }

  public Set<ValidationError> errors() {
    return errors;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void throwIfHasErrors() {
    if (!errors().isEmpty()) {
      throw new TyResException(toString());
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

  public static final class Builder {

    private Builder() {}

    @Nullable
    private Set<ValidationError> errors;

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
