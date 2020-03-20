package org.centerm.Tollway.alipay;

import android.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class CryptoServices {

//    public static String PUBLIC_PATH   = "/data/thaivan/edc-smart-payment-public-key_uat.der";
//    public static String PRIVATE_PATH = "/data/thaivan/edc-smart-payment-private-key_uat.der";

//    public static String PUBLIC_PATH   = "/data/thaivan/edc-smart-payment-public-key_real.der";
//    public static String PRIVATE_PATH = "/data/thaivan/edc-smart-payment-private-key_real.der";

    public static String AES_KEY = "TlgvEkwLJsL2iiDC";

    private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private static int ITERATIONS = 1;
    //
    public String encryptRSA(String data, String publicKeyPath) throws Exception {
        String eValue = null;

        try {
            PublicKey pubKey = this.readPublicKeyFromFile(publicKeyPath);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, pubKey);
            String valueToEnc = null;
            eValue = data;

            for(int i = 0; i < ITERATIONS; ++i) {
                byte[] encValue = cipher.doFinal(eValue.getBytes());
                eValue = Base64.encodeToString(encValue, Base64.NO_WRAP);
            }

            return eValue;
        } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException | InvalidKeySpecException | NoSuchAlgorithmException var9) {
            throw new Exception(var9.getMessage());
        }
    }

    private PublicKey readPublicKeyFromFile(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        FileInputStream fis = null;
        DataInputStream dis = null;

        PublicKey var8;
        try {
            File f = new File(fileName);
            fis = new FileInputStream(f);
            dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);
            dis.close();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            var8 = kf.generatePublic(spec);
        } finally {
            this.closeDataInputStream(dis);
            this.closeFileInputStream(fis);
        }

        return var8;
    }

    public String decryptRSA(String data, String privateKeyPath) throws Exception {
        String dValue = null;

        try {
            PrivateKey privateKey = this.readPrivateKeyFromFile(privateKeyPath);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(2, privateKey);
            dValue = null;
            String valueToDecrypt = data;

            for(int i = 0; i < ITERATIONS; ++i) {
                byte[] decordedValue = Base64.decode(valueToDecrypt, Base64.NO_WRAP);
                byte[] decValue = cipher.doFinal(decordedValue);
                dValue = new String(decValue);
                valueToDecrypt = dValue;
            }

            return dValue;
        } catch (InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException | NoSuchAlgorithmException var10) {
            throw new Exception(var10.getMessage());
        }
    }

    private PrivateKey readPrivateKeyFromFile(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        FileInputStream fis = null;
        DataInputStream dis = null;

        PrivateKey var8;
        try {
            File f = new File(fileName);
            fis = new FileInputStream(f);
            dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int)f.length()];
            dis.readFully(keyBytes);
            dis.close();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            var8 = kf.generatePrivate(spec);
        } finally {
            this.closeDataInputStream(dis);
            this.closeFileInputStream(fis);
        }

        return var8;
    }

    public String encryptAES(String plainText, String encryptionKey) throws Exception {
        try {
            Cipher cipher = this.getCipher(1, encryptionKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (BadPaddingException | InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | IllegalBlockSizeException var5) {
            var5.printStackTrace();
            throw new Exception(var5.getMessage());
        }
    }

    public String decryptAES(String encrypted, String encryptionKey) throws Exception {
        try {
            Cipher cipher = this.getCipher(2, encryptionKey);

            byte[] plainBytes = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP));
            return new String(plainBytes);
        } catch (BadPaddingException | InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | IllegalBlockSizeException var5) {
            var5.printStackTrace();
            throw new Exception(var5.getMessage());
        }
    }

    private Cipher getCipher(int cipherMode, String encryptionKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        String encryptionAlgorithm = "AES";
        Security.addProvider(new BouncyCastleProvider());
        SecretKeySpec keySpecification = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(cipherMode, keySpecification);
        return cipher;
    }

    private void closeDataInputStream(DataInputStream dataInputStream) {
        try {
            dataInputStream.close();
        } catch (Exception var3) {
            ;
        }

    }

    private void closeFileInputStream(FileInputStream fileInputStream) {
        try {
            fileInputStream.close();
        } catch (Exception var3) {
            ;
        }

    }
}
