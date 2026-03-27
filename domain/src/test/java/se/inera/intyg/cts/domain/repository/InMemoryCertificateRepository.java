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
package se.inera.intyg.cts.domain.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.inera.intyg.cts.domain.model.Certificate;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationId;

public class InMemoryCertificateRepository implements CertificateRepository {

  private final Map<TerminationId, List<Certificate>> certificatesMap = new HashMap<>();

  public int totalCount(TerminationId terminationId) {
    return certificatesMap.getOrDefault(terminationId, Collections.emptyList()).size();
  }

  @Override
  public void store(Termination termination, List<Certificate> certificatesToStore) {
    final var certificates =
        certificatesMap.getOrDefault(termination.terminationId(), new ArrayList<>());
    certificates.addAll(certificatesToStore);
    certificatesMap.put(termination.terminationId(), certificates);
  }

  @Override
  public List<Certificate> get(Termination termination) {
    return certificatesMap.getOrDefault(termination.terminationId(), Collections.emptyList());
  }

  @Override
  public void remove(Termination termination) {
    certificatesMap.remove(termination.terminationId());
  }
}
