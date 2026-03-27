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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Table(name = "export")
public class ExportEmbeddable {

  @Column(name = "total", table = "export")
  private int total;

  @Column(name = "revoked", table = "export")
  private int revoked;

  @Column(name = "password", table = "export")
  private String password;

  @Column(name = "export_time", table = "export")
  private LocalDateTime exportTime;

  @Column(name = "notification_time", table = "export")
  private LocalDateTime notificationTime;

  @Column(name = "reminder_time", table = "export")
  private LocalDateTime reminderTime;

  @Column(name = "receipt_time", table = "export")
  private LocalDateTime receiptTime;
}
