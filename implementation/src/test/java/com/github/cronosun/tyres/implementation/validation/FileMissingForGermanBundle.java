package com.github.cronosun.tyres.implementation.validation;

import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.experiment.Bin;

public interface FileMissingForGermanBundle {
  @File("will_only_be_available_for_english.txt")
  Bin fileThatIsOnlyAvailableInEnglish();
}
