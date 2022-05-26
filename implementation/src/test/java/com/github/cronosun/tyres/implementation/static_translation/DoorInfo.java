package com.github.cronosun.tyres.implementation.static_translation;

import com.github.cronosun.tyres.core.experiment.Resolvable;

public enum DoorInfo {
  OPEN(Resolvable.constant(StaticTranslationBundle.class, StaticTranslationBundle::open)),
  CLOSED(Resolvable.constant(StaticTranslationBundle.class, StaticTranslationBundle::closed)),
  FUBAR(Resolvable.constant(StaticTranslationBundle.class, StaticTranslationBundle::fubar));

  private final Resolvable display;

  DoorInfo(Resolvable display) {
    this.display = display;
  }

  public Resolvable display() {
    return display;
  }
}
