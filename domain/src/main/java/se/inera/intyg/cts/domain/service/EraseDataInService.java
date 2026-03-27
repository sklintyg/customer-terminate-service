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
package se.inera.intyg.cts.domain.service;

import se.inera.intyg.cts.domain.model.ServiceId;
import se.inera.intyg.cts.domain.model.Termination;

/**
 * Interface that must be implemented for each service that should be erased when a termination is
 * erased. The implementation is an integration to that particular service e.g. Webcert,
 * Intygstjanst or Intygsstatistik.
 */
public interface EraseDataInService {

  /**
   * Erase any data that is related to the service. For any reason that all the data is not erased
   * throw an exception informing what. The implementation will be called again, at a later point in
   * time, to try to erase the data.
   *
   * @param termination Termination which data should be erased.
   * @throws EraseException Exception to throw if the data isn't successfully erased.
   */
  void erase(Termination termination) throws EraseException;

  /**
   * The id that identify the service that the implementation erases data in. This must not change
   * once in production!
   *
   * @return ServiceId that has to be unique.
   */
  ServiceId serviceId();
}
