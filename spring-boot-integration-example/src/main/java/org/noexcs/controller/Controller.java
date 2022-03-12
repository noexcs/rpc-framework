package org.noexcs.controller;

import org.noexcs.service.StringUpperCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author noexcs
 * @since 3/13/2022 9:55 AM
 */
@RestController
public class Controller {

    StringUpperCaseService stringUpperCaseService;

    @GetMapping("/rpcTest")
    public String testService(){
        return stringUpperCaseService.upperCaseString("hello");
    }

    @Autowired
    public void setStringUpperCaseService(StringUpperCaseService stringUpperCaseService) {
        this.stringUpperCaseService = stringUpperCaseService;
    }
}
