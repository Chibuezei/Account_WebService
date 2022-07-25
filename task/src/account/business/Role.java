package account.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;



    @JsonIgnore
    private boolean Business;
    @JsonIgnore
    private boolean Administrative;

    //    public Role(String name) { this.name = name;}
    public Role(String name) {
        this.name = name;
        users = new HashSet<>();
        if (name.equals("ROLE_ADMINISTRATOR")) {
            Business = false;
            Administrative = true;
        }
        if (name.equals("ROLE_USER") || name.equals("ROLE_AUDITOR") || name.equals("ROLE_ACCOUNTANT")) {
            Business = true;
            Administrative = false;
        }
    }

    public Role() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public boolean isBusiness() {
        return Business;
    }

    public void setBusiness(boolean business) {
        Business = business;
    }

    public boolean isAdministrative() {
        return Administrative;
    }

    public void setAdministrative(boolean administrative) {
        Administrative = administrative;
    }
}
