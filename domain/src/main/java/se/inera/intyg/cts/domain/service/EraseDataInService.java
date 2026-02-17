package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.ServiceId;
import se.inera.intyg.cts.domain.model.Termination;

/**
 * Interface that must be implemented for each service that should be erased when a termination is
 * erased. The implementation is an integration to that particular service e.g. Webcert, Intygstjanst
 * or Intygsstatistik.
 */
public interface EraseDataInService {

  /**
   * Erase any data that is related to the service. For any reason that all the data is not erased
   * throw an exception informing what. The implementation will be called again, at a later point in
   * time, to try to erase the data.
   * @param termination Termination which data should be erased.
   * @throws EraseException Exception to throw if the data isn't successfully erased.
   */
  void erase(Termination termination) throws EraseException;

  /**
   * The id that identify the service that the implementation erases data in. This must not change
   * once in production!
   * @return  ServiceId that has to be unique.
   */
  ServiceId serviceId();
}
