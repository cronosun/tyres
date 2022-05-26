package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.experiment.ResInfo;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface TextBackend {
  @Nullable
  String maybeFmt(ResInfo.TextResInfo info, Object[] args, Locale locale);

  @Nullable
  String maybeText(ResInfo.TextResInfo info, Locale locale);
}
