package com.github.cronosun.tyres.implementation.static_translation;

import com.github.cronosun.tyres.core.Text;

public interface StaticTranslationBundle {
  Text closed();
  Text open();
  Text fubar();

  Text validationGivenAmountIsTooLarge();
}
