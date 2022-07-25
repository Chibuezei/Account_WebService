package account.presentation;

import account.business.Account;
import account.business.AccountService;
import account.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@Validated
//@PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
@RequestMapping("api/acct/payments")
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    UserService userService;
    @PostMapping()
    @Transactional(rollbackFor = {SQLException.class, MethodArgumentNotValidException.class, NullPointerException.class, ConstraintViolationException.class})
    public Map<String, String> addAll(
            @RequestBody
            @NotEmpty(message = "account list cannot be empty.")
            List<@Valid Account> accountList) {
        return accountService.addAll(accountList);
    }

    @PutMapping()
    public Map<String, String> putAccount(@Valid @RequestBody Account account) {
        Account account1 = accountService.findAccountByEmployeeAndAndPeriod(account.getEmployee(), account.getPeriod());
        account1.setSalary(account.getSalary());
        return accountService.modifyAccount(account1);
    }



}
