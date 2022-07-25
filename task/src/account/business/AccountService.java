package account.business;

import account.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class AccountService {
    @Autowired
    private UserService userService;
    private AccountRepository accountRepository;
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public void save (Account account){
        if(accountRepository.findAccountByEmployeeIgnoreCaseAndAndPeriod(account.getEmployee(), account.getPeriod()) != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "period exist!");
        }
        if (userService.findUserByEmail(account.getEmployee()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist!");
        }
        accountRepository.save(account);
        System.out.println(account);
    }

    public Map<String, String> addAll (List<Account> accountList){
        System.out.println(accountList);
        for (Account account1: accountList){
            save(account1);
        }
        return Map.of("status", "Added successfully!");
    }

//    public Map<String, String> add (List<Account> accountList){
//       accountRepository.save(accountList);
//        return Map.of("status", "Added successfully!");
//    }
    public Map<String, String> modifyAccount(Account account){
        accountRepository.save(account);
        return Map.of("status", "Updated successfully!");
    }
    public Account findAccountByEmployeeAndAndPeriod(String employee, String pattern){
        return accountRepository.findAccountByEmployeeIgnoreCaseAndAndPeriod(employee.toLowerCase(), pattern);
    }

    public  List<Account> findAccountByEmployeeIgnoreCase(String employee){
        return accountRepository.findAccountByEmployeeIgnoreCaseOrderByPeriodDesc(employee.toLowerCase());
    }
}
