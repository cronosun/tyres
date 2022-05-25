package com.github.cronosun.tyres.core.experiment;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Locale;

public interface Binary {
    InputStream get(Locale locale);
    @Nullable
    InputStream maybe(Locale locale);
}
