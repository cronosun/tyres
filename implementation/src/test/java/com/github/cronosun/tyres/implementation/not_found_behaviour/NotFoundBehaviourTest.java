package com.github.cronosun.tyres.implementation.not_found_behaviour;

import static org.junit.jupiter.api.Assertions.*;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class NotFoundBehaviourTest {

  @Test
  void maybeAlwaysReturnsNull() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(NotFoundBehaviourBundle.class);

    assertNull(bundle.binNotFound().maybe(Locale.ENGLISH));
    assertNull(bundle.binNotFound().maybe());

    assertNull(bundle.fmtNotFound("Argument").maybe(Locale.ENGLISH));
    assertNull(bundle.fmtNotFound("Argument").maybe());

    assertNull(bundle.textNotFound().maybe(Locale.ENGLISH));
    assertNull(bundle.textNotFound().maybe());
  }

  @Test
  void getReturnsFallbackIfConfiguredToDoSo() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.FALLBACK);
    var bundle = resources.get(NotFoundBehaviourBundle.class);

    // fallback: Cannot really test, since the fallback text is an implementation detail - but at least
    // should return a non-blank string and MUST NOT throw.

    assertFalse(bundle.fmtNotFound("Argument").get(Locale.ENGLISH).isBlank());
    assertFalse(bundle.fmtNotFound("Argument").get().isBlank());

    assertFalse(bundle.textNotFound().get(Locale.ENGLISH).isBlank());
    assertFalse(bundle.textNotFound().get().isBlank());
  }

  @Test
  void getThrowsIfConfiguredToDoSo() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(NotFoundBehaviourBundle.class);

    assertThrows(TyResException.class, () -> bundle.fmtNotFound("Argument").get(Locale.ENGLISH));
    assertThrows(TyResException.class, () -> bundle.fmtNotFound("Argument").get());
    assertThrows(TyResException.class, () -> bundle.textNotFound().get(Locale.ENGLISH));
    assertThrows(TyResException.class, () -> bundle.textNotFound().get());
  }
}
