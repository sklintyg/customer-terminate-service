package se.inera.intyg.cts.domain.service;

import java.util.Optional;
import se.inera.intyg.cts.domain.model.Termination;
import se.inera.intyg.cts.domain.model.TerminationStatus;
import se.inera.intyg.cts.domain.repository.TerminationRepository;

public class SendPackagePasswordImpl implements SendPackagePassword {

  private final SendPassword sendPassword;
  private final TerminationRepository terminationRepository;

  public SendPackagePasswordImpl(SendPassword sendPassword,
      TerminationRepository terminationRepository) {
    this.sendPassword = sendPassword;
    this.terminationRepository = terminationRepository;
  }

  /**
   * Send the password
   * <p>
   *
   * @param termination to send the password for
   * @return updated termination
   */
  @Override
  public Optional<Termination> sendPassword(Termination termination) {
    final var sendPasswordSuccess = sendPassword.sendPassword(termination);

    if (sendPasswordSuccess) {
      termination.passwordSent();
      terminationRepository.store(termination);
    }
    return terminationRepository.findByTerminationId(termination.terminationId());
  }

  /**
   * Resend the password. This can only be done if the password has been sent at least once.
   *
   * @param termination to resend the password for
   * @return updated termination
   */
  @Override
  public Termination resendPassword(Termination termination) {

    if (termination.status().equals(TerminationStatus.PASSWORD_SENT) || termination.status()
        .equals(TerminationStatus.PASSWORD_RESENT)) {
      if (sendPassword.sendPassword(termination)) {
        termination.passwordResent();
        terminationRepository.store(termination);
        return terminationRepository.findByTerminationId(termination.terminationId()).get();
      }
      throw new RuntimeException(
          "Could not store status %s for %s".formatted(TerminationStatus.PASSWORD_RESENT,
              termination.terminationId().id()));
    }
    throw new IllegalArgumentException(
        String.format("Invalid status: %s to resend password.", termination.status()));
  }
}
