package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;

public interface SimpleBundle {
    SimpleBundle INSTANCE = TyRes.create(SimpleBundle.class);

    NewText voidMethod();
    @Name("renamed_method")
    NewText renamedMethod();
    @Name("renamed_method")
    NewText renamedMethodTwo();
    NewText methodWithArgument(String arg0);
    NewText methodWithTwoArguments(String arg0, int arg1);
}
