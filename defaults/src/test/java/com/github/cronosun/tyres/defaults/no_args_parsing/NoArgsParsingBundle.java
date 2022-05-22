package com.github.cronosun.tyres.defaults.no_args_parsing;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.core.TyRes;

public interface NoArgsParsingBundle {
    NoArgsParsingBundle INSTANCE = TyRes.create(NoArgsParsingBundle.class);

    MsgRes msgRes();
    StrRes strRes();
}
