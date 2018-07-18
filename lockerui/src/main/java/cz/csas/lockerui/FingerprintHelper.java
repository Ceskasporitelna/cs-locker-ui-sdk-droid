package cz.csas.lockerui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import cz.csas.cscore.client.crypto.CryptoManager;
import cz.csas.cscore.client.crypto.CryptoManagerImpl;

/**
 * The type Fingerprint ui helper.
 *
 * @author Jan Hauser <jan.hauser@applifting.cz>
 * @since 24 /02/16.
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHelper extends FingerprintManager.AuthenticationCallback {

    // for future purpose
    private final int AUTH_FAILED_ERROR_KEY = 660;
    private final int INIT_CIPHER_ERROR_KEY = 661;
    private final int ENCRYPT_DATA_ERROR_KEY = 662;
    private final int DECRYPT_DATA_ERROR_KEY = 663;
    private final String KEY_NAME = "fingerprint_key";

    private Activity mActivity;
    private FingerprintManager mFingerprintManager;
    private Callback mCallback;
    private CancellationSignal mCancellationSignal;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;
    private CryptoManager mCryptoManager;
    private String mMigrationFingerprintHash;


    /**
     * Instantiates a new Fingerprint helper.
     *
     * @param fingerprintManager       the fingerprint manager
     * @param migrationFingerprintHash the migration fingerprint hash
     * @param callback                 the callback
     * @param activity                 the activity
     */
    public FingerprintHelper(FingerprintManager fingerprintManager, String migrationFingerprintHash, Callback callback, Activity activity) {
        mFingerprintManager = fingerprintManager;
        mActivity = activity;
        mCallback = callback;
        mCryptoManager = new CryptoManagerImpl();
        mMigrationFingerprintHash = migrationFingerprintHash;
    }

    /**
     * Is fingerprint auth available boolean.
     *
     * @return the boolean
     */
    public boolean isFingerprintAuthAvailable() {
        // permission condition should be always true
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED)
            return mFingerprintManager != null &&
                    mFingerprintManager.isHardwareDetected()
                    && mFingerprintManager.hasEnrolledFingerprints()
                    && initCipher();
        return false;
    }

    /**
     * Start listening.
     */
    public void startListening() {
        if (isFingerprintAuthAvailable()) {
            mCancellationSignal = new CancellationSignal();
            // permission condition should be always true
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED)
                mFingerprintManager.authenticate(new FingerprintManager.CryptoObject(mCipher), mCancellationSignal, 0 /* flags */, this, null);
        }
    }

    /**
     * Stop listening.
     */
    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        stopListening();
        mCallback.onError(errMsgId, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        stopListening();
        mCallback.onError(helpMsgId, helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        stopListening();
        mCallback.onError(AUTH_FAILED_ERROR_KEY, null);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        String fingerprintHash;
        if (LockerUI.getInstance().getLocker().retrieveIvSecret() == null) {
            fingerprintHash = getFingerprintHash();
            LockerUI.getInstance().getLocker().storeEncryptedSecret(encryptWithFingerprintKey(fingerprintHash));
            LockerUI.getInstance().getLocker().storeIvSecret(mCryptoManager.encodeBase64(mCipher.getIV()));
        } else
            fingerprintHash = decryptWithFingerprintKey(LockerUI.getInstance().getLocker().retrieveEncryptedSecret());
        if (fingerprintHash != null && LockerUI.getInstance().getLocker().retrieveEncryptedSecret() != null)
            mCallback.onAuthenticated(fingerprintHash);
        else {
            LockerUI.getInstance().getLocker().wipeEncryptedSecret();
            LockerUI.getInstance().getLocker().wipeIvSecret();
        }
    }

    /**
     * Init cipher boolean.
     *
     * @return the boolean
     */
    public boolean initCipher() {
        try {
            mKeyGenerator = providesKeyGenerator();
            mCipher = providesCipher();
            mKeyStore = providesKeystore();
            if (LockerUI.getInstance().getLocker().retrieveIvSecret() == null) {
                createKey();
                mKeyStore.load(null);
                SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
                mCipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                mKeyStore.load(null);
                SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
                mCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(mCryptoManager.decodeBase64(LockerUI.getInstance().getLocker().retrieveIvSecret())));
            }
            return true;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException
                | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
            mCallback.onError(INIT_CIPHER_ERROR_KEY, "Z důvodu bezpečnosti nelze provést ověření.");
            return false;
        }
    }

    private KeyStore providesKeystore() throws KeyStoreException {
        return KeyStore.getInstance("AndroidKeyStore");
    }

    private KeyGenerator providesKeyGenerator() throws NoSuchProviderException, NoSuchAlgorithmException {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
    }

    private Cipher providesCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    private void createKey() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, CertificateException, IOException {
        mKeyStore.load(null);
        mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build());
        mKeyGenerator.generateKey();
    }

    private String encryptWithFingerprintKey(String secret) {
        try {
            CryptoManager cryptoManager = new CryptoManagerImpl();
            return cryptoManager.encodeBase64(mCipher.doFinal(secret.getBytes("UTF-8")));
        } catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
            mCallback.onError(ENCRYPT_DATA_ERROR_KEY, "Z důvodu bezpečnosti nelze provést ověření.");
        }
        return null;
    }

    private String decryptWithFingerprintKey(String encryptedSecret) {
        if (encryptedSecret != null) {
            try {
                CryptoManager cryptoManager = new CryptoManagerImpl();
                return new String(mCipher.doFinal(cryptoManager.decodeBase64(encryptedSecret)), "UTF-8");
            } catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
                e.printStackTrace();
                mCallback.onError(DECRYPT_DATA_ERROR_KEY, "Z důvodu bezpečnosti nelze provést ověření.");
            }
        }
        return null;
    }

    private String getFingerprintHash() {
        if (mMigrationFingerprintHash != null)
            return mMigrationFingerprintHash;
        return mCryptoManager.generateRandomString();
    }

    /**
     * The interface Callback.
     */
    public interface Callback {

        /**
         * On authenticated.
         *
         * @param fingerprintHash the fingerprint hash
         */
        void onAuthenticated(String fingerprintHash);

        /**
         * On error.
         *
         * @param msgId the msg id
         * @param msg   the msg
         */
        void onError(int msgId, CharSequence msg);
    }
}