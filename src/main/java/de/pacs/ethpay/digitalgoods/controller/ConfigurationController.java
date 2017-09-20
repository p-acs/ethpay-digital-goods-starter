package de.pacs.ethpay.digitalgoods.controller;


import de.pacs.ethpay.digitalgoods.Shared;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/config")
public class ConfigurationController {
    private final String publicKey;

    public ConfigurationController(Shared shared) {
        this.publicKey = shared.getPublicKey();
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "text/plain")
    public String get() {
        return publicKey;
    }
}
