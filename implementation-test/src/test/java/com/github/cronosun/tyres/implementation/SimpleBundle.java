package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;

public interface SimpleBundle {
    SimpleBundle INSTANCE = TyRes.create(SimpleBundle.class);

    Res<Text> voidMethod();
    @Name("renamed_method")
    Resource renamedMethod();
    @Name("renamed_method")
    Res<Text> renamedMethodTwo();
    Resource methodWithArgument(String arg0);
    Res<Text> methodWithTwoArguments(String arg0, int arg1);
}
