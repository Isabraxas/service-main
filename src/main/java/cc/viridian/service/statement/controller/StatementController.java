package cc.viridian.service.statement.controller;

import cc.viridian.service.statement.model.AccountsRegistered;
import cc.viridian.service.statement.payload.*;
import cc.viridian.service.statement.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class StatementController {

    @Autowired
    StatementService statementService;

    @RequestMapping(method = RequestMethod.POST, value="/account")
    @ResponseBody
    public AccountsRegistered registerNewAccount(
        @RequestBody RegisterAccountPost body)
    {
        return statementService.registerNewAccount(body);
    }

    @RequestMapping("/account")
    public ListAccountsResponse listAccounts(
        @RequestParam(value= "start", required = false, defaultValue = "0") Integer start,
        @RequestParam(value= "length", required = false, defaultValue = "25") Integer length
    ) {
        return statementService.listAccounts(start, length);
    }

    @RequestMapping("/account/monthly")
    public ListAccountsResponse listAccountsMonthly(){

        return statementService.listAccountsMonthly();
    }
}
