package de.pacs.ethpay.digitalgoods;

import org.bouncycastle.util.encoders.Hex;
import org.ethereum.crypto.ECKey;
import org.kohsuke.args4j.CmdLineException;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

@Configuration
@ComponentScan({"de.pacs.ethpay.digitalgoods.controller"})
@EnableAutoConfiguration(exclude=HibernateJpaAutoConfiguration.class)
public class DigitalgoodsApplication {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static CmdLineResult cmdLineResult;
    private static Shared shared;

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        try {
            cmdLineResult = new CmdLineResult();
            cmdLineResult.parseArguments(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            System.out.println("Usage:");
            cmdLineResult.printExample();
            System.exit(-1);
            return;
        }

        ECKey key = ECKey.fromPrivate(Hex.decode(cmdLineResult.getEthereumKey()));
        String address = "0x" + Hex.toHexString(key.getAddress());
        String pubKey = Hex.toHexString(key.getPubKey());
        System.out.println("Ethereumaddress: " + address);
        System.out.println("!!!!!!!!!");
        System.out.println("!!!!!!!!!");
        System.out.println("this project is only a starter to show the use of 'ethPay instant access' ");
        System.out.println("DON'T USE IN PRODUCTION");
        System.out.println("!!!!!!!!!");
        System.out.println("!!!!!!!!!");
        shared = new Shared(pubKey);
        File fileDirectory = new File(cmdLineResult.getFileDirectory());
        if (!fileDirectory.exists()) {
            System.out.println(cmdLineResult.getFileDirectory() + " does not exist");
            System.exit(-1);
        }
        shared.setFileDirectory(fileDirectory);
        shared.setKey(key);
        shared.setIpfsGateway(cmdLineResult.getIpfsGateway());
        SpringApplication app = new SpringApplication(DigitalgoodsApplication.class);
        app.setBanner(new Banner() {
            @Override
            public void printBanner(Environment environment, Class<?> aClass, PrintStream printStream) {
                //send the Spring Boot banner to /dev/null
            }
        });
        app.run();
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.setPort(cmdLineResult.getPort());
            }
        };
    }

    @Bean
    public Shared shared() {
        return shared;
    }
}
