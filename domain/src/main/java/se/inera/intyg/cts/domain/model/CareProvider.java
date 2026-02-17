package se.inera.intyg.cts.domain.model;

public record CareProvider(HSAId hsaId, OrganizationNumber organizationNumber) {

  public CareProvider {
    if (hsaId == null) {
      throw new IllegalArgumentException("Missing HSAId");
    }
    if (organizationNumber == null) {
      throw new IllegalArgumentException("Missing OrganizationNumber");
    }
  }
}
