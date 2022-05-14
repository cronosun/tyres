package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * Give the method a custom name.
 * <p>
 * Note: You should not use this to change naming conventions, still adhere to the java method naming, such as
 * <pre>myMethodName</pre>, don't rewrite that to <pre>my_method_name</pre> or <pre>my-method-name</pre>. It's
 * up to the implementation (such as {@link MsgSource_OLD}) to convert names.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface RenameMethod {
    String value();
}
