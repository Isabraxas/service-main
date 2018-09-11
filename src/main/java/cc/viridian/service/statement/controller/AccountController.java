package cc.viridian.service.statement.controller;

import cc.viridian.service.statement.model.AccountsRegistered;
import cc.viridian.service.statement.payload.ListAccountsResponse;
import cc.viridian.service.statement.payload.RegisterAccountPost;
import cc.viridian.service.statement.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    StatementService statementService;

    @RequestMapping(method = RequestMethod.POST, value = "/account")
    @ResponseBody
    public AccountsRegistered registerNewAccount(
        @RequestBody final RegisterAccountPost body) {
        return statementService.registerNewAccount(body);
    }

    @RequestMapping("/account")
    public ListAccountsResponse listAccounts(
        @RequestParam(value = "start", required = false, defaultValue = "0") final Integer start,
        @RequestParam(value = "length", required = false, defaultValue = "25") final Integer length
    ) {
        return statementService.listAccounts(start, length);
    }

    @RequestMapping("/account/monthly")
    public ListAccountsResponse listAccountsMonthly() {

        return statementService.listAccountsMonthly();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/account/truncate")
    public Map<String, Object> processTruncate() {
        return statementService.processTruncate();
    }
}
