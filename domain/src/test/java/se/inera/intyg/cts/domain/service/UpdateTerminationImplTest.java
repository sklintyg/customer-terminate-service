package se.inera.intyg.cts.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.cts.domain.util.TerminationTestDataFactory.defaultTerminationBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.cts.domain.model.EmailAddress;
import se.inera.intyg.cts.domain.model.HSAId;
import se.inera.intyg.cts.domain.model.PersonId;
import se.inera.intyg.cts.domain.model.PhoneNumber;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.CertificateRepository;
import se.inera.intyg.cts.domain.repository.CertificateTextRepository;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

@ExtendWith(MockitoExtension.class)
class UpdateTerminationImplTest {

  @Mock
  private TerminationRepository terminationRepository;

  @Mock
  private CertificateRepository certificateRepository;

  @Mock
  private CertificateTextRepository certificateTextRepository;

  @InjectMocks
  private UpdateTerminationImpl updateTermination;

  @BeforeEach
  void setUp() {
    when(terminationRepository.store(any(Termination.class))).thenAnswer(i -> i.getArguments()[0]);
  }

  @Nested
  class UpdateHsaId {

    private HSAId newHsaId;
    private Termination updatedTermination;

    @BeforeEach
    void setUp() {
      newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.EXPORTED)
          .create();

      updatedTermination = updateTermination.update(
          termination,
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );
    }

    @Test
    void shallUpdateHsaId() {
      assertEquals(newHsaId, updatedTermination.careProvider().hsaId());
    }

    @Test
    void shallResetStatus() {
      assertEquals(TerminationStatus.CREATED, updatedTermination.status());
    }

    @Test
    void shallRemoveCollectedCertificates() {
      verify(certificateRepository, times(1)).remove(updatedTermination);
    }

    @Test
    void shallRemoveCollectedCertificateTexts() {
      verify(certificateTextRepository, times(1)).remove(updatedTermination);
    }

    @Test
    void shallStoreUpdatedTerminationInRepository() {
      final var newHsaId = new HSAId("NewHsaId");
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.EXPORTED)
          .create();

      final var updatedTermination = updateTermination.update(
          termination,
          newHsaId,
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );

      verify(terminationRepository, times(1)).store(updatedTermination);
    }
  }

  @Nested
  class UpdatePersonId {

    private PersonId newPersonId;
    private Termination updatedTermination;

    @BeforeEach
    void setUp() {
      newPersonId = new PersonId("NewPersonId");
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.EXPORTED)
          .create();

      updatedTermination = updateTermination.update(
          termination,
          termination.careProvider().hsaId(),
          newPersonId,
          termination.export().organizationRepresentative().emailAddress(),
          termination.export().organizationRepresentative().phoneNumber()
      );
    }

    @Test
    void shallUpdateHsaId() {
      assertEquals(newPersonId,
          updatedTermination.export().organizationRepresentative().personId());
    }

    @Test
    void shallResetStatus() {
      assertEquals(TerminationStatus.COLLECTING_CERTIFICATE_TEXTS_COMPLETED,
          updatedTermination.status());
    }

    @Test
    void shallStoreUpdatedTerminationInRepository() {
      verify(terminationRepository, times(1)).store(updatedTermination);
    }

    @Test
    void shallNotMakeAnyChangesToCertificates() {
      verifyNoInteractions(certificateRepository);
    }

    @Test
    void shallNotMakeAnyChangesToCertificateTexts() {
      verifyNoInteractions(certificateTextRepository);
    }
  }

  @Nested
  class UpdateEmailAddress {

    private EmailAddress newEmailAddress;
    private Termination updatedTermination;

    @BeforeEach
    void setUp() {
      newEmailAddress = new EmailAddress("NewEmailAddress");
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.NOTIFICATION_SENT)
          .create();

      updatedTermination = updateTermination.update(
          termination,
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          newEmailAddress,
          termination.export().organizationRepresentative().phoneNumber()
      );
    }

    @Test
    void shallUpdateEmailAddress() {
      assertEquals(newEmailAddress,
          updatedTermination.export().organizationRepresentative().emailAddress());
    }

    @Test
    void shallResetStatus() {
      assertEquals(TerminationStatus.EXPORTED, updatedTermination.status());
    }

    @Test
    void shallStoreUpdatedTerminationInRepository() {
      verify(terminationRepository, times(1)).store(updatedTermination);
    }

    @Test
    void shallNotMakeAnyChangesToCertificates() {
      verifyNoInteractions(certificateRepository);
    }

    @Test
    void shallNotMakeAnyChangesToCertificateTexts() {
      verifyNoInteractions(certificateTextRepository);
    }
  }

  @Nested
  class UpdatePhoneNumber {

    private Termination updatedTermination;
    private PhoneNumber newPhoneNumber;

    @BeforeEach
    void setUp() {
      newPhoneNumber = new PhoneNumber("NewPhoneNumber");
      final var termination = defaultTerminationBuilder()
          .status(TerminationStatus.NOTIFICATION_SENT)
          .create();

      updatedTermination = updateTermination.update(
          termination,
          termination.careProvider().hsaId(),
          termination.export().organizationRepresentative().personId(),
          termination.export().organizationRepresentative().emailAddress(),
          newPhoneNumber
      );
    }

    @Test
    void shallUpdatePhoneNumber() {
      assertEquals(newPhoneNumber,
          updatedTermination.export().organizationRepresentative().phoneNumber());
    }

    @Test
    void shallResetStatus() {
      assertEquals(TerminationStatus.EXPORTED, updatedTermination.status());
    }

    @Test
    void shallStoreUpdatedTerminationInRepository() {
      verify(terminationRepository, times(1)).store(updatedTermination);
    }

    @Test
    void shallNotMakeAnyChangesToCertificates() {
      verifyNoInteractions(certificateRepository);
    }

    @Test
    void shallNotMakeAnyChangesToCertificateTexts() {
      verifyNoInteractions(certificateTextRepository);
    }
  }
}