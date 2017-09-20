package de.pacs.ethpay.crypto;

import de.pacs.messaging.controller.CryptoOperations;
import de.petendi.seccoco.connector.BCConnector;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.ByteUtil;

import javax.crypto.SecretKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class CryptoOperationsImpl implements CryptoOperations {


    private static final String SIGN_MAGIC = "\u0019Ethereum Signed Message:\n";

    @Override
    public byte[] encrypt(byte[] bytes, byte[] bytes1) {
        try {
            return new EncryptionController().encrypt(bytes, bytes1);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] bytes, byte[] bytes1) {
        try {
            return new EncryptionController().decrypt(bytes, bytes1);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public SecretKey generateSecretKey() {
        return new BCConnector().generateSecretKey();
    }

    @Override
    public String ecSign(byte[] bytes, byte[] bytes1) {
        ECKey key = ECKey.fromPrivate(bytes1).decompress();
        return key.sign(bytes).toBase64();
    }

    @Override
    public String ecSignMessage(String s, byte[] bytes) {
        byte[] messageBytes = s.getBytes();
        String prefix = SIGN_MAGIC + messageBytes.length;
        byte[] toHash = ByteUtil.merge(prefix.getBytes(), messageBytes);
        byte[] hashed = HashUtil.sha3(toHash);
        ECKey key = ECKey.fromPrivate(bytes);
        return toHex(key.sign(hashed).toCanonicalised());
    }

    @Override
    public String ecRecover(byte[] bytes, String s) {
        try {
            ECKey e = ECKey.signatureToKey(bytes, s);
            return toHexString(e.getAddress());
        } catch (SignatureException var3) {
            throw new IllegalArgumentException(var3);
        }
    }


    @Override
    public byte[] sha3(byte[] bytes) {
        return HashUtil.sha3(bytes);
    }

    @Override
    public byte[] merge(byte[]... bytes) {
        return ByteUtil.merge(bytes);
    }

    @Override
    public String toHexString(byte[] bytes) {
        return Hex.toHexString(bytes);
    }

    @Override
    public byte[] decodeHexString(String s) {
        return Hex.decode(s);
    }

    @Override
    public byte[] base64Encode(byte[] bytes) {
        return Base64.encode(bytes);
    }

    @Override
    public byte[] base64Decode(String s) {
        return Base64.decode(s);
    }

    private static final String toHex(ECKey.ECDSASignature signature) {
        final String hexR = ByteUtil.toHexString(ByteUtil.bigIntegerToBytes(signature.r, 32));
        final String hexS = ByteUtil.toHexString(ByteUtil.bigIntegerToBytes(signature.s, 32));
        final String hexV = ByteUtil.toHexString(new byte[]{signature.v});
        return new StringBuilder().append("0x").append(hexR).append(hexS).append(hexV).toString();
    }
}
