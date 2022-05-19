package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ResInfo;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface BinBackend {
  static BinBackend usingResources() {
    return DefaultBinBackend.instance();
  }

  @Nullable
  InputStream maybeBin(ResInfo resInfo, Locale locale);
}
