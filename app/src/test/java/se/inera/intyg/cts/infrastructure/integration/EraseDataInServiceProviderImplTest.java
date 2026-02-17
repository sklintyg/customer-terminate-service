package se.inera.intyg.cts.infrastructure.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import se.inera.intyg.cts.domain.service.EraseDataInService;

class EraseDataInServiceProviderImplTest {

  @Test
  void shouldReturnRegisteredEraseDataInServices() {
    final var expectedServices = Arrays.asList(
        mock(EraseDataInService.class),
        mock(EraseDataInService.class),
        mock(EraseDataInService.class)
    );
    final var actualServices = new EraseDataInServiceProviderImpl(expectedServices).getServices();

    assertEquals(expectedServices.size(), actualServices.size());
    assertAll(
        () -> assertEquals(expectedServices.get(0), actualServices.get(0)),
        () -> assertEquals(expectedServices.get(1), actualServices.get(1)),
        () -> assertEquals(expectedServices.get(2), actualServices.get(2))
    );
  }
}