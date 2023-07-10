package com.tm.loanapi;

import com.tm.loanapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
public class Controller {
    @Autowired
    LoanService loanService;

    @GetMapping("/request")
    public String request(String telephone, Integer amount) {
        return loanService.request(telephone,amount);
    }
    @GetMapping("/repay")
    public String repay(String telephone, Integer amount) {
        return loanService.repay(telephone,amount);
    }
    @GetMapping("/clear")
    private String clear() {
        return loanService.clear();
    }
    @GetMapping("/dump")
    private String dump() {
        return loanService.dump();
    }
}
