package se.inera.intyg.cts.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "termination")
@SecondaryTable(name = "export", pkJoinColumns = @PrimaryKeyJoinColumn(name = "termination_id"))
public class TerminationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "termination_id")
  @JdbcTypeCode(Types.VARCHAR)
  private UUID terminationId;
  @Column(name = "created")
  private LocalDateTime created;
  @Column(name = "modified")
  private LocalDateTime modified;
  @Column(name = "creator_hsa_id")
  private String creatorHSAId;
  @Column(name = "creator_name")
  private String creatorName;
  @Column(name = "hsa_id")
  private String hsaId;
  @Column(name = "organization_number")
  private String organizationNumber;
  @Column(name = "person_id")
  private String personId;
  @Column(name = "phone_number")
  private String phoneNumber;
  @Column(name = "email_address")
  private String emailAddress;
  @Column(name = "status")
  private String status;

  @Embedded
  private ExportEmbeddable export;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "erase", joinColumns = @JoinColumn(name = "termination_id"))
  private List<EraseEmbeddable> eraseList;

}
