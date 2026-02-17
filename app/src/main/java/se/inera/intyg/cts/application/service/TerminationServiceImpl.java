package se.inera.intyg.cts.application.service;

import static se.inera.intyg.cts.application.dto.TerminationDTOMapper.toDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.cts.application.dto.CreateTerminationDTO;
import se.inera.intyg.cts.application.dto.TerminationDTO;
import se.inera.intyg.cts.application.dto.TerminationDTOMapper;
import se.inera.intyg.cts.application.dto.UpdateTerminationDTO;
import se.inera.intyg.cts.domain.model.EmailAddress;
import se.inera.intyg.cts.domain.model.HSAId;
import se.inera.intyg.cts.domain.model.PersonId;
import se.inera.intyg.cts.domain.model.PhoneNumber;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationBuilder;
import se.inera.intyg.cts.domain.model.TerminationId;
import se.inera.intyg.cts.domain.repository.TerminationRepository;
import se.inera.intyg.cts.domain.service.SendPackagePassword;
import se.inera.intyg.cts.domain.service.UpdateTermination;

@Service
public class TerminationServiceImpl implements TerminationService {

  private static final Logger LOG = LoggerFactory.getLogger(TerminationServiceImpl.class);

  private final TerminationRepository terminationRepository;
  private final SendPackagePassword sendPackagePassword;
  private final UpdateTermination updateTermination;

  public TerminationServiceImpl(TerminationRepository terminationRepository,
      SendPackagePassword sendPackagePassword,
      UpdateTermination updateTermination) {
    this.terminationRepository = terminationRepository;
    this.sendPackagePassword = sendPackagePassword;
    this.updateTermination = updateTermination;
  }

  @Override
  @Transactional
  public TerminationDTO create(CreateTerminationDTO createTerminationDTO) {

    final var termination = TerminationBuilder.getInstance()
        .creatorHSAId(createTerminationDTO.creatorHSAId())
        .creatorName(createTerminationDTO.creatorName())
        .careProviderHSAId(createTerminationDTO.hsaId())
        .careProviderOrganizationNumber(createTerminationDTO.organizationNumber())
        .careProviderOrganizationRepresentativePersonId(createTerminationDTO.personId())
        .careProviderOrganizationRepresentativePhoneNumber(createTerminationDTO.phoneNumber())
        .careProviderOrganizationRepresentativeEmailAddress(createTerminationDTO.emailAddress())
        .create();

    final var createdTermination = terminationRepository.store(termination);
    LOG.info("Created termination with id '{}' for care provider '{}'",
        createdTermination.terminationId().id(),
        createdTermination.careProvider().hsaId().id()
    );

    return toDTO(createdTermination);
  }

  @Override
  @Transactional
  public Optional<TerminationDTO> findById(UUID terminationId) {
    final var termination = terminationRepository.findByTerminationId(
        new TerminationId(terminationId));

    return termination.map(TerminationDTOMapper::toDTO);
  }

  @Override
  @Transactional
  public List<TerminationDTO> findAll() {
    return terminationRepository.findAll().stream()
        .map(TerminationDTOMapper::toDTO)
        .toList();
  }

  /**
   * Try to resend password termination exists
   * @param terminationId Id of the termination to resend.
   * @return TerminationDTO containing the new status.
   * @throws IllegalArgumentException When termination does not exist
   */
  @Override
  @Transactional
  public TerminationDTO resendPassword(UUID terminationId) throws IllegalArgumentException {
    Termination termination = terminationRepository.findByTerminationId(new TerminationId(terminationId)).orElseThrow(
        () -> new IllegalArgumentException (String.format("Termination for id %s not found", terminationId))
      );
      return TerminationDTOMapper.toDTO(sendPackagePassword.resendPassword(termination));
  }

  @Override
  @Transactional
  public TerminationDTO update(UUID terminationId, UpdateTerminationDTO updateTerminationDTO) {
    final var termination = terminationRepository.findByTerminationId(
        new TerminationId(terminationId)).orElseThrow(
        () -> new IllegalArgumentException(
            String.format("TerminationId '%s' doesn't exist!", terminationId)
        )
    );

    final var updatedTermination = updateTermination.update(
        termination,
        new HSAId(updateTerminationDTO.hsaId()),
        new PersonId(updateTerminationDTO.personId()),
        new EmailAddress(updateTerminationDTO.emailAddress()),
        new PhoneNumber(updateTerminationDTO.phoneNumber())
    );

    return toDTO(updatedTermination);
  }

}
