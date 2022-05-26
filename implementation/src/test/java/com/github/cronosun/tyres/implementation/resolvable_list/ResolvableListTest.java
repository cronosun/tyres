package com.github.cronosun.tyres.implementation.resolvable_list;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.ResolvableList;
import com.github.cronosun.tyres.implementation.TestUtil;
import com.github.cronosun.tyres.implementation.WorkingBundle;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResolvableListTest {

  @Test
  void simpleResolvableList() {
    var resources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);

    var elements = List.of(
      WorkingBundle.INSTANCE.colour(),
      WorkingBundle.INSTANCE.somethingFromParent(),
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("TheArgument")
    );
    var list = ResolvableList.from(elements);
    var messageString = resources.resolver().get(list, Locale.UK);

    Assertions.assertEquals(
      "Colour, Message from parent interface, Hello, TheArgument!",
      messageString
    );
  }

  @Test
  void singleElementResolvableList() {
    var resources = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);

    var elements = List.of(WorkingBundle.INSTANCE.colour());
    var list = ResolvableList.from(elements);
    var messageString = resources.resolver().get(list, Locale.UK);

    Assertions.assertEquals("Colour", messageString);
  }

  @Test
  void emptyResolvableList() {
    var source = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);

    var list = ResolvableList.empty();
    var messageString = source.resolver().get(list, Locale.UK);

    Assertions.assertEquals("", messageString);
  }

  @Test
  void resolvableListWithCustomConfigurationMultipleItems() {
    var source = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);

    var elements = List.of(
      WorkingBundle.INSTANCE.colour(),
      WorkingBundle.INSTANCE.somethingFromParent(),
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("TheArgument")
    );
    var list = ResolvableList.from(CustomResolvableListConfiguration.INSTANCE, elements);

    var messageEn = source.resolver().get(list, Locale.UK);
    Assertions.assertEquals(
      "\"Colour, Message from parent interface, Hello, TheArgument!\"",
      messageEn
    );

    var messageDe = source.resolver().get(list, Locale.GERMAN);
    Assertions.assertEquals(
      "<<Farbe; Meldung vom Elter-Interface; Hallo, TheArgument!>>",
      messageDe
    );
  }

  @Test
  void resolvableListWithCustomConfigurationEmptyList() {
    var source = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);

    var list = ResolvableList.from(CustomResolvableListConfiguration.INSTANCE, List.of());

    var messageEn = source.resolver().get(list, Locale.UK);
    Assertions.assertEquals("Nothing in this list", messageEn);

    var messageDe = source.resolver().get(list, Locale.GERMAN);
    Assertions.assertEquals("Nichts in der Liste", messageDe);
  }

  @Test
  void resolvableListWithCustomConfigurationOneItemInList() {
    var source = TestUtil.newImplementation(MsgNotFoundStrategy.THROW);

    var elements = List.of(WorkingBundle.INSTANCE.colour());
    var list = ResolvableList.from(CustomResolvableListConfiguration.INSTANCE, elements);

    var messageEn = source.resolver().get(list, Locale.UK);
    Assertions.assertEquals("Only one: [Colour]", messageEn);

    var messageDe = source.resolver().get(list, Locale.GERMAN);
    Assertions.assertEquals("Nur eines: [Farbe]", messageDe);
  }
}
