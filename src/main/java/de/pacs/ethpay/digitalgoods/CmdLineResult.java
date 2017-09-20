package de.pacs.ethpay.digitalgoods;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

public class CmdLineResult {


    @Option(name = "--files", usage = "The working directory (default /opt/ethpay)")
    private String fileDirectory = "/opt/ethpay";

    @Option(name = "--port", usage = "The port the application is listening on (default 8080)")
    private int port = 8080;


    @Option(name = "--ipfs", usage = "The url to the IPFS gateway (default https://ipfs.io)")
    private String ipfsGateway = "https://ipfs.io";

    @Option(name = "--key", required = true, usage = "The private key of the Ethereum identity used for decrypting receipts")
    private String ethereumKey = null;

    public String getFileDirectory() {
        return fileDirectory;
    }

    public int getPort() {
        return port;
    }


    public String getIpfsGateway() {
        return ipfsGateway;
    }

    public String getEthereumKey() {
        return ethereumKey;
    }

    void parseArguments(String[] arguments) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        parser.parseArgument(arguments);
    }

    void printExample() {
        CmdLineParser parser = new CmdLineParser(this, ParserProperties.defaults().withShowDefaults(false));
        parser.printUsage(System.out);
    }
}
