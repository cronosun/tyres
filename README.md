# TyRes - Typed Resources

## Introduction

Provides functionality for typed resources (such as messages) for [spring](https://spring.io/) applications or other Java applications.

### Main features
 
 * Typed resources using interfaces.
 * Supports arguments for messages.
 * Multiple backend implementations possible (such as Spring `MessageSource` or plain Java `ResourceBundle`).
 * Lightweight & small.
 * Validation: Missing translations or unused translations.
 * Testing: Easy to test whether services return correct messages.
 * Globally configurable what to do if a resource is missing (return a fallback message or throw an exception).

### How does it look like?

Write a message bundle (it's just an interface):

```java
public interface MyMessages {
    MyMessages INSTANCE = TyRes.create(MyMessages.class);
    
    MsgRes amountTooLarge(int amount);
    MsgRes missingAmount();
}
```

... then add a resource (`MyMessages.properties` in the correct package):

```properties
amountTooLarge=Given amount {0,number,integer} is too large!
missingAmount=Amount is required
```

... then translate the messages:

```java
import java.util.Locale;

class TranslateTest {
    // Get instance, see DefaultResources (inject if using spring)
    private final Resources resources;
    
    public void testTranslations() {
        var msg1 = resources.msg(MyMessages.missingAmount(), Locale.UK);
        assertEquals("Amount is required", msg1);

        var msg2 = resources.msg(MyMessages.amountTooLarge(2232), Locale.UK);
        assertEquals("Given amount 2232 is too large!", msg2);
    }
}
```

## Common errors

### Resource not found

Depending on the implementation, the `.properties`-file must be located in different locations. Say the bundle interface is called `com.company.MyBundle`, the `.properties`-file must be located here:

 * For JVM resource bundle implementation: It must be called `MyBundle.properties` and must be placed inside the **directory** `com/company` in the `resources` directory.
 * For the Spring implementation: Spring wants the file to be in the `resources`-directory, called `com.company.MyBundle.properties`.
