package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MsgListTest {

  @Test
  void simpleMessageList() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var messages = List.of(
      WorkingBundle.INSTANCE.colour(),
      WorkingBundle.INSTANCE.somethingFromParent(),
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("TheArgument")
    );
    var list = MsgList.fromList(messages);
    var messageString = resources.msg().resolve(list, Locale.UK);

    Assertions.assertEquals(
      "Colour, Message from parent interface, Hello, TheArgument!",
      messageString
    );
  }

  @Test
  void emptyMessageList() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var list = MsgList.empty();
    var messageString = source.msg().resolve(list, Locale.UK);

    Assertions.assertEquals("", messageString);
  }

  @Test
  void messageListWithCustomConfiguration() {
    var source = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    var messages = List.of(
      WorkingBundle.INSTANCE.colour(),
      WorkingBundle.INSTANCE.somethingFromParent(),
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("TheArgument")
    );
    var list = MsgList.fromList(CustomMsgListConfiguration.INSTANCE, messages);

    var messageEn = source.msg().resolve(list, Locale.UK);
    Assertions.assertEquals(
      "\"Colour, Message from parent interface, Hello, TheArgument!\"",
      messageEn
    );

    var messageDe = source.msg().resolve(list, Locale.GERMAN);
    Assertions.assertEquals(
      "<<Farbe; Meldung vom Elter-Interface; Hallo, TheArgument!>>",
      messageDe
    );
  }
}
