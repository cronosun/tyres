package com.github.cronosun.tyres.implementation.resolvable_list;

import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.experiment.ResolvableList;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResolvableListTest {

  @Test
  void simpleResolvableList() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(ResolvableListBundle.class);

    var elements = List.of(
            bundle.colour(),
            bundle.aluminium(),
            bundle.somethingWithArgument("TheArgument")
    );
    var list = ResolvableList.from(elements);
    var messageString = resources.resolve(list).get(Locale.UK);

    Assertions.assertEquals(
      "Colour, Aluminium, Hello, TheArgument!",
      messageString
    );
  }

  @Test
  void singleElementResolvableList() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(ResolvableListBundle.class);

    var elements = List.of(bundle.colour());
    var list = ResolvableList.from(elements);
    var messageString = resources.resolve(list).get(Locale.UK);

    Assertions.assertEquals("Colour", messageString);
  }

  @Test
  void emptyResolvableList() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);

    var list = ResolvableList.empty();
    var messageString = resources.resolve(list).get(Locale.UK);

    Assertions.assertEquals("", messageString);
  }

  @Test
  void resolvableListWithCustomConfigurationMultipleItems() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(ResolvableListBundle.class);

    var elements = List.of(
            bundle.colour(),
            bundle.aluminium(),
            bundle.somethingWithArgument("TheArgument")
    );

    var list = ResolvableList.from(CustomResolvableListConfiguration.class, elements);

    var messageEn = resources.resolve(list).get(Locale.UK);
    Assertions.assertEquals(
      "\"Colour, Aluminium, Hello, TheArgument!\"",
      messageEn
    );

    var messageDe = resources.resolve(list).get(Locale.GERMAN);
    Assertions.assertEquals(
      "<<Farbe; Aluminium; Hallo, TheArgument!>>",
      messageDe
    );
  }

  @Test
  void resolvableListWithCustomConfigurationEmptyList() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var list = ResolvableList.from(CustomResolvableListConfiguration.class, List.of());

    var messageEn = resources.resolve(list).get(Locale.UK);
    Assertions.assertEquals("Nothing in this list", messageEn);

    var messageDe = resources.resolve(list).get( Locale.GERMAN);
    Assertions.assertEquals("Nichts in der Liste", messageDe);
  }

  @Test
  void resolvableListWithCustomConfigurationOneItemInList() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(ResolvableListBundle.class);
    var elements = List.of(
            bundle.colour()
    );
    var list = ResolvableList.from(CustomResolvableListConfiguration.class, elements);

    var messageEn = resources.resolve(list).get(Locale.UK);
    Assertions.assertEquals("Only one: [Colour]", messageEn);

    var messageDe = resources.resolve(list).get(Locale.GERMAN);
    Assertions.assertEquals("Nur eines: [Farbe]", messageDe);
  }
}
