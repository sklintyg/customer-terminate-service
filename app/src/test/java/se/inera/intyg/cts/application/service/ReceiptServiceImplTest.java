/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.cts.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceImplTest {

  private static final UUID TERMINATION_UUID = UUID.randomUUID();
  @Spy Termination termination = defaultTermination();
  @Mock private TerminationRepository terminationRepository;
  @InjectMocks private ReceiptServiceImpl receiptServiceImpl;

  @Test
  void testHandleReceipt() {
    when(terminationRepository.findByTerminationId(any(TerminationId.class)))
        .thenReturn(Optional.of(termination));

    receiptServiceImpl.handleReceipt(TERMINATION_UUID);

    verify(terminationRepository, times(1)).findByTerminationId(any(TerminationId.class));
    verify(termination, times(1)).receiptReceived(any(LocalDateTime.class));
    verify(terminationRepository, times(1)).store(termination);
  }

  @Test()
  void testHandleTerminationNotFound() {
    when(terminationRepository.findByTerminationId(any(TerminationId.class)))
        .thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> receiptServiceImpl.handleReceipt(TERMINATION_UUID));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }
}
