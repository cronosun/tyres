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
* Does not require the latest LTS (Java 17) - also works with Java 11.

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

        // get the bundle - can also be injected, if you're using spring or a different DI framework.
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
        
        // messages can be compared without actually producing text; useful for tests.
        var expected = bundle.amountTooLarge(333);
        var actual = bundle.amountTooLarge(333);
        assertEquals(expected, actual);
    }
    
    // Messages can be referenced in static context (and resolved later).
    enum MyEnum {
        MISSING(Resolvable.constant(MyMessages.class, MyMessages::missingAmount)),
        NOT_FORMATTED(Resolvable.constant(MyMessages.class, MyMessages::notFormatted));

        MyEnum(Resolvable displayName) {
          // <...>    
        }
        // <...>
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

### Alternatives

The closest alternative I could find
is [Apache DeltaSpike, i18n](https://deltaspike.apache.org/documentation/core.html#Messagesandi18n). As far as I can
tell it does not support those things / has those drawbacks (but I could be wrong):

* It's integrated in a framework, spring integration seems tricky (at least I could not find documentation on how to do
  that), don't know whether it works in a standalone Java application or in an android application.
* Missing validation: Does not detect missing translations / malformed message patterns (unless you "produce" the
  message).
* Lacks some minor features such as `Localized`, `ResolvableList` & `Resolvable`. 
* No support for localized binary resources.

So if you want a mature framework, such as [Apache DeltaSpike](https://deltaspike.apache.org/), use it. If you want a
small library with some additional features, use TyRes.

### Performance / overhead

This describes the operations the default implementation (see `implementation` module) performs.
 
#### Getting a bundle for the first time: `T Resources#get(Class<T>)`

```java
interface MyBundle {
    Text myText();
    Fmt formatted(String argument);
}

Resources resources = resources();
// It's about this operation: getting a bundle
var myBundle = resources.get(MyBundle.class);
```

This is the most expensive operation - but will only be performed once per bundle class (`MyBundle.class` in the example); Subsequent calls to `T Resources#get(Class<T>)` will just return the already created bundle.

 * Uses reflection to query the interface
 * Constructs the model (see `BundleInfo` and `EntryInfo`).
 * Creates a proxy.
 * Stores the bundle in a concurrent hash map.

Subsequent calls to `Resources#get` then just return the already created bundle from a concurrent hash map. If you're using a DI framework, you can store the bundle (or store it somewhere else, as it does not change).

#### Getting a text from a bundle (no arguments)

```java
Resources resources = resources();
var myBundle = resources.get(MyBundle.class);
// It's about this operation: getting text from a bundle
var text = myBundle.myText();
```

This operation performs a call to `Map<String, Text>#get(String)` performed by the proxy, where `String` is the method name. No memory allocation involved here.

#### Getting a formatted text with arguments from a bundle

```java
Resources resources = resources();
var myBundle = resources.get(MyBundle.class);
// It's about this operation: getting text with arguments from a bundle
var text = myBundle.formatted("The argument");
```

This is similar to getting a text without arguments but additionally involves one allocation: Creates a new `Fmt` object with the argument(s) (in the example `The argument`).

#### Resolving the text

```java
Resources resources = resources();
var myBundle = resources.get(MyBundle.class);
var text = myBundle.myText();
// It's about this operation: resolving the text
var resolvedText = text.get(Locale.US);
```

This is now nothing more than calling the backend (such as Java's `ResourceBundle` and Java's `MessageFormat`).

#### Validation

Validation is a heavy operation and should not be performed in production (only to be enabled in dev-builds, local development and unit-tests).

#### Summary

 * If you save the bundle using DI (or save it somewhere else) for messages without arguments, this is the overhead:
   * One call to `Map<String, Object>#get(String)` (performed by the proxy).
 * Formatted messages with at least one argument additionally require one allocation.
 * If you don't store the bundle and call `T Resources#get(Class<T>)`, one call to `ConcurrentHashMap<Class, T>#get(Class)` is performed.
 * DO NOT perform validation in production builds.

## Legal

### Header logo

["Tyre"](https://www.flickr.com/photos/9937638@N02/5698711393)
by [mahela1993](https://www.flickr.com/photos/9937638@N02) is licensed
under [CC BY-SA 2.0](https://creativecommons.org/licenses/by-sa/2.0/). Changes were made, original photo can be
found [here](https://www.flickr.com/photos/9937638@N02/5698711393).

### License

TyRes is licensed under [Apache License, Version 2.0](LICENSE.md).

