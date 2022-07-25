package account.persistence;

import account.business.Role;
import account.business.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
//    @Override
//    Optional<Role> findById(Long aLong);

    Optional<Role> findByName(String name);

}

