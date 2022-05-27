package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.File;

public interface FileMissingForGermanBundle {
  @File("will_only_be_available_for_english.txt")
  Bin fileThatIsOnlyAvailableInEnglish();
}
