package com.kingspan.challenge.requests;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class purchaseRequestController {
    @RequestMapping
    public String index() {
        return "index";
    }

}
