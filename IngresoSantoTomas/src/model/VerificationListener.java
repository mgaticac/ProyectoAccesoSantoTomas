package model;

import java.util.Optional;

@FunctionalInterface
public interface VerificationListener {

    void verificationEvent(Optional<FPUser> user);
}
