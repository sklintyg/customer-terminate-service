package se.inera.intyg.cts.infrastructure.integration.Intygstjanst.dto;

import java.util.List;

public record CertificateExportPageDTO(String careProviderId, int count, long total,
                                       long totalRevoked, List<CertificateXmlDTO> certificateXmls) {

}