package com.github.cronosun.tyres.core.experiment;

public class Demo {

    static void test(NewResouces x) {
        String str = x.get(DemoBundle.class).myMessage().get();
        String str2 = x.get(DemoBundle.class).myMessageWithThing("hello").get();

        var resolvable = Resolvable.of(DemoBundle.class, DemoBundle::myMessage);
        var resolvable2 = Resolvable.of(DemoBundle.class, bundle -> bundle.myMessageWithThing("Hello"));
    }
}
