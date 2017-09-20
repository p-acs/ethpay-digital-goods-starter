package de.pacs.ethpay.digitalgoods.controller;

import de.pacs.ethpay.digitalgoods.Shared;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

@Controller
@RequestMapping("/")
public class RootController {

    private final Shared shared;
    private final byte[] pubKeyBytes;

    public RootController(Shared shared) {
        this.shared = shared;
        pubKeyBytes = Hex.decode(shared.getPublicKey());
    }


    @ResponseBody()
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Resource get(@RequestParam("ethpay") String ethpay, HttpServletResponse response) {
        try {
            AuthenticationController authenticationController = new AuthenticationController(shared.getIpfsGateway(), shared.getKey().getPrivKeyBytes(), pubKeyBytes);
            String extractedFilename = authenticationController.extractFileName(ethpay);
            File file = new File(shared.getFileDirectory(), extractedFilename);
            if (file.exists()) {
                response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
                return new FileSystemResource(file);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (AuthenticationController.OutofSyncException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (AuthenticationController.SignaturesDontMatchException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (SocketTimeoutException e) {
            response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        System.err.println("Could not serve request: " + ethpay + " status " + response.getStatus());
        return null;
    }

}
