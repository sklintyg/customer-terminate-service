package se.inera.intyg.cts.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certificate_text")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateTextEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "certificate_type")
  private String certificateType;
  @Column(name = "certificate_type_version")
  private String certificateTypeVersion;
  @Column(name = "xml")
  private String xml;
  @ManyToOne(fetch = FetchType.LAZY)
  private TerminationEntity termination;

}
