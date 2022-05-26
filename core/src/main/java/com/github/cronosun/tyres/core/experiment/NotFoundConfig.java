package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.TyResException;
import org.jetbrains.annotations.Nullable;

/**
 * Depending on the situation you might want to configure the application's "not found strategy" differently. For
 * Unit-Tests (and maybe also local development) you might want to throw exceptions to detect missing or invalid
 * resources, for production builds you might want to use fallbacks.
 */
public enum NotFoundConfig {
  /**
   * If there's no such message, throws a {@link TyResException} exception.
   */
  THROW(WithNullAndDefault.THROW),

  /**
   * If there's no such message, returns the fallback value.
   */
  FALLBACK(WithNullAndDefault.FALLBACK),

  /**
   * Do whatever is configured by the implementation; see {@link DefaultNotFoundConfig}.
   */
  DEFAULT(WithNullAndDefault.DEFAULT);

  private final WithNullAndDefault withNullAndDefault;

  NotFoundConfig(WithNullAndDefault withNullAndDefault) {
    this.withNullAndDefault = withNullAndDefault;
  }

  public WithNullAndDefault withNullAndDefault() {
    return withNullAndDefault;
  }

  public WithNullNoDefault toWithNull(DefaultNotFoundConfig defaultNotFoundConfig) {
    var withNull = this.withNullAndDefault.withNullNoDefault;
    if (withNull != null) {
      return withNull;
    } else {
      return defaultNotFoundConfig.withNull();
    }
  }

  /**
   * A version of {@link NotFoundConfig} that includes {@link WithNullNoDefault#NULL} but does not include
   * {@link WithNullNoDefault#DEFAULT}.
   */
  public enum WithNullNoDefault {
    /**
     * See {@link NotFoundConfig#THROW}
     */
    THROW,
    /**
     * See {@link NotFoundConfig#FALLBACK}
     */
    FALLBACK,
    /**
     * If the resource (or a component of the resource) cannot be found: return <code>null</code>.
     */
    NULL;

    public WithNullAndDefault withNullAndDefault() {
      switch (this) {
        case THROW:
          return WithNullAndDefault.THROW;
        case FALLBACK:
          return WithNullAndDefault.FALLBACK;
        case NULL:
          return WithNullAndDefault.NULL;
        default:
          throw new IllegalArgumentException("Unknown type: " + this);
      }
    }
  }

  public enum WithNullAndDefault {
    THROW(WithNullNoDefault.THROW),
    FALLBACK(WithNullNoDefault.FALLBACK),
    DEFAULT(null),
    NULL(WithNullNoDefault.NULL);

    @Nullable
    private final NotFoundConfig.WithNullNoDefault withNullNoDefault;

    WithNullAndDefault(@Nullable NotFoundConfig.WithNullNoDefault withNullNoDefault) {
      this.withNullNoDefault = withNullNoDefault;
    }

    public NotFoundConfig.WithNullNoDefault withNullNoDefault(
      DefaultNotFoundConfig defaultNotFoundConfig
    ) {
      var withNullNoDefault = this.withNullNoDefault;
      if (withNullNoDefault != null) {
        return withNullNoDefault;
      } else {
        return defaultNotFoundConfig.withNull();
      }
    }
  }
}
