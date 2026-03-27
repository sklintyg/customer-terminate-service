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
package se.inera.intyg.cts.infrastructure.integration.tellustalk;

import static se.inera.intyg.cts.logging.MdcLogConstants.EVENT_TYPE_INFO;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import se.inera.intyg.cts.infrastructure.integration.SendSMS;
import se.inera.intyg.cts.infrastructure.integration.tellustalk.dto.SMSRequestDTO;
import se.inera.intyg.cts.infrastructure.integration.tellustalk.dto.TellusTalkResponseDTO;
import se.inera.intyg.cts.logging.PerformanceLogging;

@Service
public class SendSMSWithTellusTalk implements SendSMS {

  @Value("${sms.originator.text}")
  private String smsOriginatorText;

  private final WebClient webClient;
  private final String scheme;
  private final String baseUrl;
  private final int port;
  private final String tellustalkSendEndpoint;

  private final String username;
  private final String password;

  public SendSMSWithTellusTalk(
      @Qualifier(value = "tellusTalkWebClient") WebClient webClient,
      @Value("${integration.tellustalk.scheme}") String scheme,
      @Value("${integration.tellustalk.baseurl}") String baseUrl,
      @Value("${integration.tellustalk.port}") int port,
      @Value("${integration.tellustalk.send.endpoint}") String tellustalkSendEndpoint,
      @Value("${integration.tellustalk.username}") String username,
      @Value("${integration.tellustalk.password}") String password) {
    this.webClient = webClient;
    this.scheme = scheme;
    this.baseUrl = baseUrl;
    this.port = port;
    this.tellustalkSendEndpoint = tellustalkSendEndpoint;
    this.username = username;
    this.password = password;
  }

  @Override
  @PerformanceLogging(eventAction = "send-sms", eventType = EVENT_TYPE_INFO)
  public TellusTalkResponseDTO sendSMS(String phonenumber, String message) {
    SMSRequestDTO smsRequestDTO = new SMSRequestDTO(phonenumber, message, smsOriginatorText);

    return webClient
        .post()
        .uri(
            uriBuilder ->
                uriBuilder
                    .scheme(scheme)
                    .host(baseUrl)
                    .port(port)
                    .path(tellustalkSendEndpoint)
                    .build())
        .body(Mono.just(smsRequestDTO), SMSRequestDTO.class)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .headers(headers -> headers.setBasicAuth(username, password))
        .retrieve()
        .bodyToMono(TellusTalkResponseDTO.class)
        .share()
        .block();
  }
}
