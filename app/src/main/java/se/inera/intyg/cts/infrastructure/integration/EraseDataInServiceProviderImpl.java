package se.inera.intyg.cts.infrastructure.integration;

import java.util.List;
import org.springframework.stereotype.Service;
import se.inera.intyg.cts.domain.service.EraseDataInService;
import se.inera.intyg.cts.domain.service.EraseDataInServiceProvider;

@Service
public class EraseDataInServiceProviderImpl implements EraseDataInServiceProvider {

  private final List<EraseDataInService> services;

  public EraseDataInServiceProviderImpl(List<EraseDataInService> services) {
    this.services = services;
  }

  @Override
  public List<EraseDataInService> getServices() {
    return services;
  }
}
