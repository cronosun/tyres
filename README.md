![Header Logo](imgs/header_logo.webp)

# TyRes - Typed Resources

## Introduction

Provides functionality for typed resources (such as messages) for [spring](https://spring.io/) applications or other
Java applications.

### Main features

* Typed resources using interfaces.
* Supports arguments for messages.
* Supports 3 types of resources:
    * **Formatted**: Formatted messages, with 0-n arguments (see Java's `MessageFormat`).
    * **Text**: Plain string, not formatted and 0 arguments.
    * **Binary** resources (localization is supported too).
* Supports multiple backend implementations: Spring `MessageSource` and Java `ResourceBundle` are included - or write
  your own.
* Lightweight & small.
* Validation: Detect missing translations or unused translations.
* Testing: Easy to test whether services return correct messages (messages can be referenced in tests).
* Globally configurable what to do if a resource is missing (return a fallback message or throw an exception).

### What does it look like?

Write a message bundle (it's just an interface):

```java
public interface MyMessages {
    Fmt amountTooLarge(int amount);
    Text missingAmount();
    Text notFormatted();
    @File("some_data.png")
    Bin someBinary();
}
```

... then add the `MyMessages.properties` and `some_data.png` in the correct package:

```properties
amountTooLarge=Given amount {0,number,integer} is too large!
missingAmount=Amount is required!
notFormatted=Something that's not formatted.
```

... then translate the messages:

```java
import java.util.Locale;

class TranslateTest {
    // Get instance / inject if using spring (see tests on how to obtain an instance).
    private final Resources resources;

    public void testTranslations() {
        // validate the bundle (will for example detect missing translations).
        resources.validate(MyMessages.class, Set.of(Locale.UK));
        
        var bundle = resources.get(MyMessages.class);
        
        var msg1 = bundle.missingAmount().get(Locale.UK);
        assertEquals("Amount is required!", msg1);

        var msg2 = bundle.amountTooLarge(2232).get(Locale.UK);
        assertEquals("Given amount 2232 is too large!", msg2);

        // if you don't specify a locale, the locale from the locale provider is taken.
        var msg3 = bundle.notFormatted().get();
        assertEquals("Something that's not formatted.", msg3);

        // binaries can be localized too.
        var inputStream = bundle.someBinary().get(Locale.UK);
        assertNotNull(inputStream);
        inputStream.close();
    }
}
```

## Modules

* `core`: Contains the API and a default implementation of `TyResImplementation`. You always need this dependency.
* `implementation`: Contains the default implementation - you most likely want this dependency too (unless you write
  your own
  implementation from scratch).
* `spring`: Contains an implementation for spring. You only need this implementation if you're using spring.
* `kotlin`: Currently does not contain code, just some tests to make sure the library also works flawlessly with kotlin.

## More

### Documentation

See the [tests](implementation/src/test/java/com/github/cronosun/tyres/implementation/README.md) in the `implementation`
module, they are
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

### Why are there two resource types that produce strings?

There's `Text` and `Fmt`: Other libraries / frameworks work differently, [spring](https://spring.io/) for example
does not (unless configured otherwise) format (using `MessageFormat`) if there are no arguments and formats the message
if there are arguments. Why doesn't TyRes work the same instead of having two resource types (`Text` and `Fmt`)?
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

By using `Text` and `Fmt` instead of `UnspecifiedRes`, the validator knows.

### I want all the resources in one single `.properties`-file

This is possible, see tests and `EffectiveNameGenerator`. You can also implement your own `EffectiveNameGenerator`: This
allows low level control:

* What `.properties`-file to use.
* How the keys in the `.properties`-file look like.

## Legal

### Header logo

["Tyre"](https://www.flickr.com/photos/9937638@N02/5698711393) by [mahela1993](https://www.flickr.com/photos/9937638@N02) is licensed under [CC BY-SA 2.0](https://creativecommons.org/licenses/by-sa/2.0/). Changes were made, original photo can be found [here](https://www.flickr.com/photos/9937638@N02/5698711393).



