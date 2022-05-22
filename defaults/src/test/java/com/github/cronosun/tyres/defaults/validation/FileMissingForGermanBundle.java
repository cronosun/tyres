package com.github.cronosun.tyres.defaults.validation;

import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.TyRes;

public interface FileMissingForGermanBundle {
  FileMissingForGermanBundle INSTANCE = TyRes.create(FileMissingForGermanBundle.class);

  @File("will_only_be_available_for_english.txt")
  Resources.BinRes fileThatIsOnlyAvailableInEnglish();
}
