package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.ResInfo;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Locale;

public interface BinBackend {
    /**
     * Maybe get the binary.
     */
    @Nullable
    InputStream maybeBin(ResInfo.Bin info, Locale locale);
}
