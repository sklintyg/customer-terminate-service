package se.inera.intyg.cts.infrastructure.persistence.entity;

import se.inera.intyg.cts.domain.model.EraseService;
import se.inera.intyg.cts.domain.model.ServiceId;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationBuilder;
import se.inera.intyg.cts.domain.model.TerminationStatus;

public class TerminationEntityMapper {

  private TerminationEntityMapper() {
    throw new IllegalStateException("Mapper class");
  }

  public static TerminationEntity toEntity(Termination termination) {
    return new TerminationEntity(
        0L,
        termination.terminationId().id(),
        termination.created(),
        termination.modified(),
        termination.creator().hsaId().id(),
        termination.creator().name(),
        termination.careProvider().hsaId().id(),
        termination.careProvider().organizationNumber().number(),
        termination.export().organizationRepresentative().personId().id(),
        termination.export().organizationRepresentative().phoneNumber().number(),
        termination.export().organizationRepresentative().emailAddress().emailAddress(),
        termination.status().name(),
        new ExportEmbeddable(
            termination.export().certificateSummary().total(),
            termination.export().certificateSummary().revoked(),
            termination.export().password() != null ? termination.export().password().password()
                : null,
            termination.export().exportTime(),
            termination.export().notificationTime(),
            termination.export().reminderTime(),
            termination.export().receiptTime()
        ),
        termination.erase().eraseServices().stream()
            .map(eraseService ->
                new EraseEmbeddable(
                    eraseService.serviceId().id(),
                    eraseService.erased())
            )
            .toList()
    );
  }

  public static Termination toDomain(TerminationEntity terminationEntity) {
    return TerminationBuilder.getInstance()
        .terminationId(terminationEntity.getTerminationId())
        .created(terminationEntity.getCreated())
        .modified(terminationEntity.getModified())
        .creatorHSAId(terminationEntity.getCreatorHSAId())
        .creatorName(terminationEntity.getCreatorName())
        .careProviderHSAId(terminationEntity.getHsaId())
        .careProviderOrganizationNumber(terminationEntity.getOrganizationNumber())
        .careProviderOrganizationRepresentativePersonId(
            terminationEntity.getPersonId())
        .careProviderOrganizationRepresentativePhoneNumber(terminationEntity.getPhoneNumber())
        .careProviderOrganizationRepresentativeEmailAddress(terminationEntity.getEmailAddress())
        .status(TerminationStatus.valueOf(terminationEntity.getStatus()))
        .total(terminationEntity.getExport().getTotal())
        .revoked(terminationEntity.getExport().getRevoked())
        .packagePassword(terminationEntity.getExport().getPassword())
        .exportTime(terminationEntity.getExport().getExportTime())
        .notificationTime(terminationEntity.getExport().getNotificationTime())
        .reminderTime(terminationEntity.getExport().getReminderTime())
        .receiptTime(terminationEntity.getExport().getReceiptTime())
        .eraseServices(
            terminationEntity.getEraseList().stream()
                .map(eraseEmbeddable ->
                    new EraseService(
                        new ServiceId(eraseEmbeddable.getServiceId()),
                        eraseEmbeddable.isErased()
                    ))
                .toList()
        )
        .create();
  }
}
