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
package se.inera.intyg.cts.infrastructure.integration.Intygstjanst;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_ACCESSED;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import se.inera.intyg.cts.domain.model.CertificateText;
import se.inera.intyg.cts.domain.model.CertificateType;
import se.inera.intyg.cts.domain.model.CertificateTypeVersion;
import se.inera.intyg.cts.domain.model.CertificateXML;
import se.inera.intyg.cts.infrastructure.integration.GetCertificateTexts;
import se.inera.intyg.cts.infrastructure.integration.Intygstjanst.dto.CertificateTextDTO;
import se.inera.intyg.cts.logging.PerformanceLogging;

@Service
public class GetCertificateTextsFromIntygstjanst implements GetCertificateTexts {

  private final WebClient webClient;
  private final String scheme;
  private final String baseUrl;
  private final String port;
  private final String certificateTextsEndpoint;

  public GetCertificateTextsFromIntygstjanst(
      @Qualifier(value = "intygstjanstWebClient") WebClient webClient,
      @Value("${integration.intygstjanst.scheme}") String scheme,
      @Value("${integration.intygstjanst.baseurl}") String baseUrl,
      @Value("${integration.intygstjanst.port}") String port,
      @Value("${integration.intygstjanst.certificate.texts.endpoint}")
          String certificateTextsEndpoint) {
    this.webClient = webClient;
    this.scheme = scheme;
    this.baseUrl = baseUrl;
    this.port = port;
    this.certificateTextsEndpoint = certificateTextsEndpoint;
  }

  @Override
  @PerformanceLogging(eventAction = "get-certificate-texts", eventType = EVENT_TYPE_ACCESSED)
  public List<CertificateText> get() {
    final List<CertificateTextDTO> certificateExportPageDTOMono =
        webClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .scheme(scheme)
                        .host(baseUrl)
                        .port(port)
                        .path(certificateTextsEndpoint)
                        .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<CertificateTextDTO>>() {})
            .share()
            .block();

    return certificateExportPageDTOMono.stream()
        .map(
            certificateTextDTO ->
                new CertificateText(
                    new CertificateType(certificateTextDTO.type()),
                    new CertificateTypeVersion(certificateTextDTO.version()),
                    new CertificateXML(certificateTextDTO.xml())))
        .collect(Collectors.toList());
  }
}
