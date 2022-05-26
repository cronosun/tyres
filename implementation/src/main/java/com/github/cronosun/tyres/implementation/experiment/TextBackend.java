package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.experiment.ResInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@ThreadSafe
public interface TextBackend {
    @Nullable
    String maybeFmt(ResInfo.Text info, Object[] args, Locale locale);
    @Nullable
    String maybeText(ResInfo.Text info, Locale locale);
}
