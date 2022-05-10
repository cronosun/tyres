package com.github.cronosun.tyres;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBundleTest {

    @Test
    void voidMethodIsCorrect() {
        var res = SimpleBundle.INSTANCE.voidMethod();
        var info = res.info();
        var method = info.method();

        assertEquals(0, res.args().length);
        assertNull(info.customPackage());
        assertEquals("voidMethod", method.getName());
        assertEquals(SimpleBundle.class, method.getDeclaringClass());
        assertEquals(SimpleBundle.class, info.bundleClass());
        assertEquals(method.getName(), info.name());
    }

    @Test
    void methodWithArgumentIsCorrect() {
        var res = SimpleBundle.INSTANCE.methodWithArgument("Hello");
        var info = res.info();
        var method = info.method();

        assertEquals(1, res.args().length);
        assertEquals("Hello", res.args()[0]);
        assertNull(info.customPackage());
        assertEquals("methodWithArgument", method.getName());
        assertEquals(SimpleBundle.class, method.getDeclaringClass());
        assertEquals(SimpleBundle.class, info.bundleClass());
        assertEquals(method.getName(), info.name());
    }

    @Test
    void methodCanBeRenamed1() {
        var res = SimpleBundle.INSTANCE.renamedMethod();
        var info = res.info();

        assertEquals("renamed_method", info.name());
    }

    @Test
    void methodCanBeRenamed2() {
        var res = SimpleBundle.INSTANCE.renamedMethodTwo();
        var info = res.info();

        assertEquals("renamed_method", info.name());
    }
}