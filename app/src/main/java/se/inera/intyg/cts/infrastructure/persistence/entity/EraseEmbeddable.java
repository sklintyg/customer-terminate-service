package se.inera.intyg.cts.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Table(name = "erase")
public class EraseEmbeddable {

  @Column(name = "service_id", table = "erase")
  private String serviceId;
  @Column(name = "erased", table = "erase")
  private boolean erased;

}
