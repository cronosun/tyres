# TyRes - Typed Resources

## Introduction

Provides functionality for typed resources (such as messages) for [spring](https://spring.io/) applications or other
Java applications.

### Main features

* Typed resources using interfaces.
* Supports arguments for messages.
* Supports 3 types of resources:
    * **Messages**: Messages are formatted patterns with 0-n arguments (see Java's `MessageFormat`).
    * **Strings**: Similar to messages, but not formatted, without arguments.
    * **Binary** resources (localization is supported too).
* Supports multiple backend implementations: Spring `MessageSource` and Java `ResourceBundle` are included - or write
  your own.
* Lightweight & small (few dependencies).
* Validation: Detect missing translations or unused translations.
* Testing: Easy to test whether services return correct messages (messages can be referenced in tests).
* Globally configurable what to do if a resource is missing (return a fallback message or throw an exception).

### What does it look like?

Write a message bundle (it's just an interface):

```java
public interface MyMessages {
    MyMessages INSTANCE = TyRes.create(MyMessages.class);

    MsgRes amountTooLarge(int amount);

    MsgRes missingAmount();

    StrRes notFormatted();

    @File("some_data.png")
    BinRes someBinary();
}
```

... then add the `MyMessages.properties` and `some_data.png` in the correct package:

```properties
amountTooLarge=Given amount {0,number,integer} is too large!
missingAmount=Amount is required!
notFormatted=Somethig that's not formatted.
```

... then translate the messages:

```java
import java.util.Locale;

class TranslateTest {
    // Get instance, see ResourcesConstructor (inject if using spring)
    private final Resources resources;

    public void testTranslations() {
        var msg1 = resources.msg().get(MyMessages.INSTANCE.missingAmount(), Locale.UK);
        assertEquals("Amount is required!", msg1);

        var msg2 = resources.msg().get(MyMessages.INSTANCE.amountTooLarge(2232), Locale.UK);
        assertEquals("Given amount 2232 is too large!", msg2);

        var msg3 = resources.resolver().get(MyMessages.INSTANCE.notFormatted(), Locale.UK);
        assertEquals("Somethig that's not formatted.", msg3);

        var inputStream = resources.bin().get(MyMessages.INSTANCE.someBinary());
        assertNotNull(inputStream);
        inputStream.close();
    }
}
```

## Modules

* `core`: Contains the API and a default implementation of `TyResImplementation`. You always need this dependency.
* `implementation`: Contains the default implementation - you most likely want this dependency too (unless you write your own
  implementation from scratch).
* `spring`: Contains an implementation for spring. You only need this implementation if you use spring.

## More

### Documentation

See the [tests](implementation/src/test/java/com/github/cronosun/tyres/implementation/README.md) in the `implementation` module, they are
also meant as documentation. Also
see [the spring tests](spring/src/test/java/com/github/cronosun/tyres/spring/README.md) if you're interested in spring
support.

### Validation

TyRes can validate resource bundles and finds those errors:

* Missing resource / missing translation / missing binary for given locale.
* Invalid message patterns (invalid format and incorrect number of arguments).
* Superfluous / unused texts in `.properties`-files.

## FAQ & common errors

### Resource not found

Depending on the implementation, the `.properties`-files must be located in different locations. Say the bundle
interface is called `com.company.MyBundle`, the `.properties`-file must be located here:

* For JVM resource bundle implementation: It must be called `MyBundle.properties` and must be placed inside the **
  directory** `com/company` in the `resources` directory.
* For the Spring implementation: Some spring backends (the `ReloadableResourceBundleMessageSource`) wants the file to be
  in the `resources`-directory, called `com.company.MyBundle.properties`.

### Custom bundle layout

You might want to have all your resources in one single `messages.properties` instead of one properties-file per bundle.
That's currently not implemented (but might be implemented eventually). In the meantime, you can implement that
yourself: see for example `MsgStrBackend`. Note: Du not abuse the `@RenamePackage` annotation to achieve that (
validation won't work anymore if you solve this that way).

### Why are there two resource types that produce strings?

There's `MsgRes` and `StrRes`: Other libraries / frameworks work differently, [spring](https://spring.io/) for example
does not (unless configured otherwise) format (using `MessageFormat`) if there are no arguments and formats the message
if there are arguments. Why doesn't TyRes work the same instead of having two resource types (`StrRes` and `MsgRes`)?
It's for validation reasons: Say we have this situation:

```java
interface MyBundle {
    UnspecifiedRes fileNotFound();
}
```

```properties
fileNotFound=File {0} not found.
```

... how should the validator know what's correct?

* Maybe the developer has forgotten the filename in the argument and the method should look like
  this `UnspecifiedRes fileNotFound(String filename)`?
* Or is this intentional and the developer really wants the literal text `File {0} not found.`?

By using `StrRes` and `MsgRes` instead of `UnspecifiedRes`, the validator knows.

### Why use a static `INSTANCE` field instead of spring injection?

A simple bundle looks like this:

```java
interface MyBundle {
    MyBundle INSTANCE = TyRes.create(MyBundle.class);

    MsgRes myMessage(String information);

    StrRes enumConstantOneDisplayName();
}
```

Why not let spring create the instance (for example using a `FactoryBean`) instead of
using `INSTANCE = TyRes.create(MyBundle.class)`? While this was technically possible, there are some cases where you
can't get the spring context. Say for example you want to do something like this:

```java
enum MyEnum {
    CONSTANT_ONE(MyBundle.INSTANCE.enumConstantOneDisplayName());
    /// <...>   
}
```

... and it's also not possible for non-spring applications / applications that don't use DI. So by using `INSTANCE`, we
don't lose much but gain uniformity (all bundles look the same and are instantiated the same). 