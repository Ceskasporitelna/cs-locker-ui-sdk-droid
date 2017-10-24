package cz.csas.lockerui.config;

import cz.csas.cscore.locker.Password;
import cz.csas.cscore.locker.PasswordMigrationProcess;

/**
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 19/09/2017.
 */
public class MigrationFlowOptions {

    private Password password;
    private PasswordMigrationProcess passwordMigrationProcess;
    private String clientId;
    private String deviceFingerprint;
    private String encryptionKey;
    private String oneTimePasswordKey;
    private String refreshToken;

    public MigrationFlowOptions(Password password, PasswordMigrationProcess passwordMigrationProcess, String clientId, String deviceFingerprint, String encryptionKey, String oneTimePasswordKey, String refreshToken) {
        this.password = password;
        this.passwordMigrationProcess = passwordMigrationProcess;
        this.clientId = clientId;
        this.deviceFingerprint = deviceFingerprint;
        this.encryptionKey = encryptionKey;
        this.oneTimePasswordKey = oneTimePasswordKey;
        this.refreshToken = refreshToken;
    }

    public MigrationFlowOptions(Builder builder) {
        this.password = builder.password;
        this.passwordMigrationProcess = builder.passwordMigrationProcess;
        this.clientId = builder.clientId;
        this.deviceFingerprint = builder.deviceFingerprint;
        this.encryptionKey = builder.encryptionKey;
        this.oneTimePasswordKey = builder.oneTimePasswordKey;
        this.refreshToken = builder.refreshTokenKey;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public PasswordMigrationProcess getPasswordMigrationProcess() {
        return passwordMigrationProcess;
    }

    public void setPasswordMigrationProcess(PasswordMigrationProcess passwordMigrationProcess) {
        this.passwordMigrationProcess = passwordMigrationProcess;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getOneTimePasswordKey() {
        return oneTimePasswordKey;
    }

    public void setOneTimePasswordKey(String oneTimePasswordKey) {
        this.oneTimePasswordKey = oneTimePasswordKey;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public static class Builder {

        private Password password;
        private PasswordMigrationProcess passwordMigrationProcess;
        private String clientId;
        private String deviceFingerprint;
        private String encryptionKey;
        private String oneTimePasswordKey;
        private String refreshTokenKey;

        public Builder setPassword(Password password) {
            this.password = password;
            return this;
        }

        public Builder setPasswordMigrationProcess(PasswordMigrationProcess passwordMigrationProcess) {
            this.passwordMigrationProcess = passwordMigrationProcess;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setEncryptionKey(String encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        public Builder setDeviceFingerprint(String deviceFingerprint) {
            this.deviceFingerprint = deviceFingerprint;
            return this;
        }

        public Builder setOneTimePasswordKey(String oneTimePasswordKey) {
            this.oneTimePasswordKey = oneTimePasswordKey;
            return this;
        }

        public Builder setRefreshTokenKey(String refreshTokenKey) {
            this.refreshTokenKey = refreshTokenKey;
            return this;
        }

        public MigrationFlowOptions create() {
            return new MigrationFlowOptions(this);
        }
    }
}
