package se.inera.intyg.cts.infrastructure.integration;

import static se.inera.intyg.cts.logging.MdcHelper.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.cts.logging.MdcHelper.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.cts.logging.MdcLogConstants.SESSION_ID_KEY;
import static se.inera.intyg.cts.logging.MdcLogConstants.TRACE_ID_KEY;

import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class ExchangeFilterFunctionProvider {

  private ExchangeFilterFunctionProvider() {
    throw new IllegalStateException("Utility class");
  }

  public static ExchangeFilterFunction addHeadersFromMDCToRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(
        request -> Mono.just(ClientRequest.from(request)
            .headers(httpHeaders -> {
              httpHeaders.add(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY));
              httpHeaders.add(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY));
            })
            .build())
    );
  }
}
