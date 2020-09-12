package service;

import model.FPSensor;
import model.FPUser;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.sql.SQLException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FPSensorVerificationService {

    public final static Logger log = Logger.getLogger(FPSensor.class.getName());

    static {
        log.setLevel(Level.FINEST); // nivel de logging
    }
    private FPUserService fpUserService;

    public FPSensorVerificationService(FPUserService userService) {
        this.fpUserService = userService;
    }

    public Optional<FPUser> verify(DPFPFeatureSet featureSet) throws ClassNotFoundException, SQLException {

        DPFPVerification matcher = DPFPGlobal.getVerificationFactory().createVerification();
        matcher.setFARRequested(DPFPVerification.MEDIUM_SECURITY_FAR);
        Map<FPUser, DPFPVerificationResult> trueVerificationsResults = new HashMap<>();

        log.info("Verifying user in user database...");
        for (FPUser fpUser : fpUserService.getAllUsers()) {
            DPFPVerificationResult verify = matcher.verify(featureSet, fpUser.getTemplate());
            log.info("Verified user id: '" + fpUser.getUserId() + "'\tFalse accept Rate: '" + verify.getFalseAcceptRate() + "'");

            if (verify.isVerified()) {
                double FAR = (double) verify.getFalseAcceptRate() / DPFPVerification.PROBABILITY_ONE;
                fpUser.setFAR(FAR);
                trueVerificationsResults.put(fpUser, verify);
            }
        }

        if (trueVerificationsResults.isEmpty()) {
            log.info("No users match");
            return Optional.empty();
        }
        if (trueVerificationsResults.size() == 1) {
            FPUser key = trueVerificationsResults.entrySet()
                    .stream()
                    .findFirst()
                    .get()
                    .getKey();
            log.info("User match: " + key);
            return Optional.of(key);
        } else {
            log.warning("More than 1 user founded! -> Adjust your match FAR requested");
            int i = 0;
            FPUser minUser = null;
            for (Map.Entry<FPUser, DPFPVerificationResult> entry : trueVerificationsResults.entrySet()) {
                FPUser user = entry.getKey();
                if (i == 0) {
                    minUser = user;
                } else {
                    if (minUser.getFAR() < user.getFAR()) {
                        minUser = user;
                    }
                }
                i++;
            }
            log.info("Returning user min FAR user:" + minUser);
            return Optional.of(minUser);
        }
    }

}
