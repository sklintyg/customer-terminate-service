package se.inera.intyg.cts.infrastructure.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntityMapper.toDomain;
import static se.inera.intyg.cts.infrastructure.persistence.entity.TerminationEntityMapper.toEntity;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATED;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_CREATOR_NAME;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_HSA_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_MODIFIED;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_ORGANIZATION_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PERSON_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_PHONE_NUMBER;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_STATUS;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.DEFAULT_TERMINATION_ID;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTermination;
import static se.inera.intyg.cts.testutil.TerminationTestDataBuilder.defaultTerminationEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.cts.domain.model.Termination;

class TerminationEntityMapperTest {

  @Nested
  class ToEntity {

    private Termination termination;

    @BeforeEach
    void setUp() {
      termination = defaultTermination();
    }

    @Test
    void shallMapTerminationId() {
      assertEquals(termination.terminationId().id(), toEntity(termination).getTerminationId());
    }

    @Test
    void shallMapHSAId() {
      assertEquals(DEFAULT_HSA_ID, toEntity(termination).getHsaId());
    }

    @Test
    void shallMapOrganizationNumber() {
      assertEquals(DEFAULT_ORGANIZATION_NUMBER, toEntity(termination).getOrganizationNumber());
    }

    @Test
    void shallMapPersonId() {
      assertEquals(DEFAULT_PERSON_ID, toEntity(termination).getPersonId());
    }

    @Test
    void shallMapPhoneNumber() {
      assertEquals(DEFAULT_PHONE_NUMBER, toEntity(termination).getPhoneNumber());
    }

    @Test
    void shallMapCreated() {
      assertEquals(termination.created(), toEntity(termination).getCreated());
    }

    @Test
    void shallMapModified() {
      assertEquals(termination.modified(), toEntity(termination).getModified());
    }

    @Test
    void shallMapCreator() {
      assertEquals(DEFAULT_CREATOR_HSA_ID, toEntity(termination).getCreatorHSAId());
    }

    @Test
    void shallMapCreatorName() {
      assertEquals(DEFAULT_CREATOR_NAME, toEntity(termination).getCreatorName());
    }

    @Test
    void shallStatus() {
      assertEquals(DEFAULT_STATUS.name(), toEntity(termination).getStatus());
    }
  }

  @Nested
  class ToDomain {

    private TerminationEntity terminationEntity;

    @BeforeEach
    void setUp() {
      terminationEntity = defaultTerminationEntity();
    }

    @Test
    void shallMapTerminationId() {
      assertEquals(DEFAULT_TERMINATION_ID, toDomain(terminationEntity).terminationId().id());
    }

    @Test
    void shallMapHSAId() {
      assertEquals(DEFAULT_HSA_ID, toDomain(terminationEntity).careProvider().hsaId().id());
    }

    @Test
    void shallMapOrganizationNumber() {
      assertEquals(DEFAULT_ORGANIZATION_NUMBER,
          toDomain(terminationEntity).careProvider().organizationNumber().number());
    }

    @Test
    void shallMapPersonId() {
      assertEquals(DEFAULT_PERSON_ID,
          toDomain(terminationEntity).export().organizationRepresentative().personId().id());
    }

    @Test
    void shallMapPhoneNumber() {
      assertEquals(DEFAULT_PHONE_NUMBER,
          toDomain(terminationEntity).export().organizationRepresentative().phoneNumber()
              .number());
    }

    @Test
    void shallMapCreated() {
      assertEquals(DEFAULT_CREATED, toDomain(terminationEntity).created());
    }

    @Test
    void shallMapModified() {
      assertEquals(DEFAULT_MODIFIED, toDomain(terminationEntity).modified());
    }

    @Test
    void shallMapCreator() {
      assertEquals(DEFAULT_CREATOR_HSA_ID, toDomain(terminationEntity).creator().hsaId().id());
    }

    @Test
    void shallMapCreatorName() {
      assertEquals(DEFAULT_CREATOR_NAME, toDomain(terminationEntity).creator().name());
    }

    @Test
    void shallStatus() {
      assertEquals(DEFAULT_STATUS, toDomain(terminationEntity).status());
    }
  }
}