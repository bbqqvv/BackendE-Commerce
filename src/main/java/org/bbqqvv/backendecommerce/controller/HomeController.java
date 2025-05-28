package org.bbqqvv.backendecommerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${frontend.url}")
    private String frontendUrl;

    @GetMapping("/")
    public String redirectToFrontend() {
        return "redirect:" + frontendUrl;
    }
}
