package se.inera.intyg.cts.domain.model;

public record EraseService(ServiceId serviceId, boolean erased) {

  public EraseService {
    if (serviceId == null) {
      throw new IllegalArgumentException("ServiceId cannot be null!");
    }
  }
}
