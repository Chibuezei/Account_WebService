package account.presentation;

import account.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@RestController
@Validated
@RequestMapping("/api")
public class UserController {
    private PasswordEncoder passwordEncoder;


    //    private User user;
    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    @Autowired
    AccountService accountService;

//    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ACCOUNTANT') or hasRole('ROLE_AUDITOR')")
    @GetMapping("/empl/payment")
    public ResponseEntity getByFilter(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "period", required = false) String period) {
        if (period != null) {
            return new ResponseEntity<>(returnAccount(List.of(accountService.findAccountByEmployeeAndAndPeriod(userDetails.getUsername().toLowerCase(), period)))
                    .get(0), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(returnAccount(accountService.findAccountByEmployeeIgnoreCase(userDetails.getUsername())), HttpStatus.OK);
        }
    }

//    @PreAuthorize("hasRole('ROLE_AUDITOR')")
    @GetMapping("/security/events")
    public ResponseEntity getAllLogs(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "period", required = false) String period) {
        return new ResponseEntity<>(logService.getAllLogs(), HttpStatus.OK);
    }

    private List<AccountReturn> returnAccount(List<Account> accountList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        List<AccountReturn> accountReturns = new ArrayList<>();
        for (Account account1 : accountList) {
            User user = userService.findUserByEmail(account1.getEmployee());
            AccountReturn accountReturn = new AccountReturn();
            YearMonth date = YearMonth.parse(account1.getPeriod(), formatter);
            accountReturn.setPeriod(StringUtils.capitalize(String.valueOf(date.getMonth()).toLowerCase()) + "-" + date.getYear());
            StringBuilder salary = new StringBuilder(account1.getSalary().toString());
            if (salary.length() <= 2) salary.insert(salary.length() - 2, 0);
            salary.insert(salary.length() - 2, " dollar(s) ");
            salary.append(" cent(s)");
            accountReturn.setSalary(salary.toString());
            accountReturn.setLastname(user.getLastname());
            accountReturn.setName(user.getName());
            accountReturns.add(accountReturn);
        }

        return accountReturns;
    }


}
