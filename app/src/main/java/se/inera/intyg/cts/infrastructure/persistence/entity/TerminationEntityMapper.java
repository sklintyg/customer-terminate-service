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
        null,
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
            termination.export().password() != null
                ? termination.export().password().password()
                : null,
            termination.export().exportTime(),
            termination.export().notificationTime(),
            termination.export().reminderTime(),
            termination.export().receiptTime()),
        termination.erase().eraseServices().stream()
            .map(
                eraseService ->
                    new EraseEmbeddable(eraseService.serviceId().id(), eraseService.erased()))
            .toList());
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
        .careProviderOrganizationRepresentativePersonId(terminationEntity.getPersonId())
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
                .map(
                    eraseEmbeddable ->
                        new EraseService(
                            new ServiceId(eraseEmbeddable.getServiceId()),
                            eraseEmbeddable.isErased()))
                .toList())
        .create();
  }
}
