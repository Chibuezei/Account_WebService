package account.persistence;

import account.business.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findAccountByEmployeeIgnoreCaseOrderByPeriodDesc(String email);
    Account findAccountByEmployeeIgnoreCaseAndAndPeriod(String employee, String pattern);
//    List<Account> save (List<Account> accountList) ;

}
