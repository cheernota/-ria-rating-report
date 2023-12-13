package com.cheernota.riaratingreport.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Hidden controller to redirect Swagger UI.
 */
@Hidden
@Controller
public class RootController {

    @GetMapping(value = "/")
    public String root() {
        return "redirect:swagger-ui/index.html";
    }
}
