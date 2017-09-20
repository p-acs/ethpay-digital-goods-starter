package de.pacs.ethpay.digitalgoods;

import org.ethereum.crypto.ECKey;

import java.io.File;

public class Shared {
    private final String publicKey;
    private String ipfsGateway;
    private File fileDirectory;
    private ECKey key;

    public Shared(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public File getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(File fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public void setKey(ECKey key) {
        this.key = key;
    }

    public ECKey getKey() {
        return key;
    }

    public String getIpfsGateway() {
        return ipfsGateway;
    }

    public void setIpfsGateway(String ipfsGateway) {
        this.ipfsGateway = ipfsGateway;
    }
}
