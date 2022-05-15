package com.github.cronosun.tyres.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.MsgSource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class MsgListTest {

  @Test
  void simpleMessageList() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var messages = List.of(
      WorkingBundle.INSTANCE.colour(),
      WorkingBundle.INSTANCE.somethingFromParent(),
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("TheArgument")
    );
    var list = MsgList.fromList(messages);
    var messageString = source.message(list, Locale.UK);

    assertEquals("Colour, Message from parent interface, Hello, TheArgument!", messageString);
  }

  @Test
  void emptyMessageList() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var list = MsgList.empty();
    var messageString = source.message(list, Locale.UK);

    assertEquals("", messageString);
  }

  @Test
  void messageListWithCustomConfiguration() {
    var source = DefaultMsgSource.newWithDefaults(MsgSource.NotFoundStrategy.THROW);

    var messages = List.of(
      WorkingBundle.INSTANCE.colour(),
      WorkingBundle.INSTANCE.somethingFromParent(),
      WorkingBundle.INSTANCE.somethingFromParentWithArgument("TheArgument")
    );
    var list = MsgList.fromList(CustomMsgListConfiguration.INSTANCE, messages);

    var messageEn = source.message(list, Locale.UK);
    assertEquals("\"Colour, Message from parent interface, Hello, TheArgument!\"", messageEn);

    var messageDe = source.message(list, Locale.GERMAN);
    assertEquals("<<Farbe; Meldung vom Elter-Interface; Hallo, TheArgument!>>", messageDe);
  }
}
