package de.pacs.ethpay.digitalgoods.controller;

import de.pacs.ethpay.crypto.CryptoOperationsImpl;
import de.pacs.ethpay.model.EthpayURI;
import de.pacs.ipfs.Ipfs;
import de.pacs.messaging.controller.MessagingController;
import de.pacs.messaging.proto.ProtobufMessage;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;


public class AuthenticationController {

    //1 hour
    public static final long VALID_TIMEFRAME = 3600000;

    static class SignaturesDontMatchException extends RuntimeException {

    }

    static class OutofSyncException extends RuntimeException {

    }

    private final String ipfsGateway;
    private final byte[] ethereumKey;
    private final byte[] pubKey;

    public AuthenticationController(String ipfsGateway, byte[] ethereumKey, byte[] pubKey) {
        this.ipfsGateway = ipfsGateway;
        this.ethereumKey = ethereumKey;
        this.pubKey = pubKey;
    }

    String extractFileName(String parameter) throws IOException {
        String[] parts = parameter.split("\\.");
        String firstPart = parts[0];
        String signature = parts[1];
        String[] splitted = firstPart.split("-");
        String storeAddress = splitted[0];
        String purchaseReceipt = splitted[1];
        String timeStampString = splitted[2];
        long timeStamp = Long.parseLong(timeStampString);
        if (Math.abs(System.currentTimeMillis() - timeStamp) > VALID_TIMEFRAME) {
            throw new OutofSyncException();
        }
        byte[] receipt = Ipfs.cat(ipfsGateway + "/ipfs/", purchaseReceipt);
        MessagingController messagingController = new MessagingController(new CryptoOperationsImpl());
        ProtobufMessage.EncryptedData encryptedData = ProtobufMessage.EncryptedData.parseFrom(receipt);
        ProtobufMessage.Data data = messagingController.decrypt(storeAddress, encryptedData, ethereumKey, pubKey);
        byte[] authenticationKey = data.getMappingsMap().get("authenticationKey").toByteArray();
        try {
            String computedSignature = hmac(authenticationKey, firstPart);
            if (computedSignature.equals(signature)) {
                EthpayURI ethpayURI = new EthpayURI(data.getMappingsMap().get("ethpayUrl").toStringUtf8());
                return ethpayURI.getSuggestedData();
            } else {
                throw new SignaturesDontMatchException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }


    public static String hmac(byte[] key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }

}
