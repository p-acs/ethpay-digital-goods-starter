package de.pacs.ethpay.crypto;

import de.petendi.commons.crypto.AsymmetricCrypto;
import de.petendi.commons.crypto.connector.CryptoException;
import de.petendi.commons.crypto.connector.SecurityProviderConnector;
import org.ethereum.crypto.ECKey;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.math.ec.ECPoint;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

public class EncryptionController {

    private class MySecurityProviderConnector implements SecurityProviderConnector {

        @Override
        public X509Certificate createCertificate(String dn, String issuer, String crlUri, PublicKey publicKey, PrivateKey privateKey) throws CryptoException {
            throw new IllegalArgumentException();
        }

        @Override
        public void writeCertificate(Writer pemWriter, X509Certificate selfCert) throws IOException {
            throw new IllegalArgumentException();

        }

        @Override
        public byte[] hash(byte[] input) {
            throw new IllegalArgumentException();
        }

        @Override
        public X509Certificate extractCertificate(Reader pemReader) throws CryptoException {
            throw new IllegalArgumentException();

        }

        @Override
        public PublicKey extractPublicKey(Reader pemReader) throws CryptoException {
            throw new IllegalArgumentException();
        }

        @Override
        public String getProviderName() {
            return "SC";
        }

        @Override
        public String getCryptoAlgorithm() {
            return "ECIES";
        }

        @Override
        public String getSignAlgorithm() {
            throw new IllegalArgumentException();
        }

        @Override
        public KeyPair generateKeyPair() {
            throw new IllegalArgumentException();

        }

        @Override
        public SecretKey generateSecretKey() {
            throw new IllegalArgumentException();
        }

        @Override
        public byte[] base64Encode(byte[] toEncode) {
            throw new IllegalArgumentException();
        }

        @Override
        public byte[] base64Decode(byte[] toDecode) {
            throw new IllegalArgumentException();
        }
    }

    private final KeyFactory keyFactory;
    private final ECParameterSpec ecParameterSpec;

    public EncryptionController() {
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ECDomainParameters domain = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(domain, new SecureRandom());
        generator.init(keygenParams);
        ecParameterSpec = new ECParameterSpec(params.getCurve(), params.getG(), params.getN());
        try {
            keyFactory = KeyFactory.getInstance("EC", "SC");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchProviderException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public byte[] encrypt(byte[] plainText, byte[] publicKey) throws InvalidKeySpecException {
        ECPoint ecPoint = ECKey.CURVE.getCurve().decodePoint(publicKey);
        PublicKey publicKeyObj = keyFactory.generatePublic(new ECPublicKeySpec(ecPoint, ecParameterSpec));
        AsymmetricCrypto asymmetricCrypto = new AsymmetricCrypto(new MySecurityProviderConnector());
        return asymmetricCrypto.encrypt(plainText, publicKeyObj);
    }

    public byte[] decrypt(byte[] encrypted, byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKeyObj = keyFactory.generatePrivate(new ECPrivateKeySpec(new BigInteger(1, privateKey), ecParameterSpec));
        AsymmetricCrypto asymmetricCrypto = new AsymmetricCrypto(new MySecurityProviderConnector());
        return asymmetricCrypto.decrypt(encrypted, privateKeyObj);
    }
}
