package account.presentation;

import account.business.Role;
import account.business.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;


public class UserDTO {
    @JsonIgnore
    private User user;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail().toLowerCase();
        this.roles = user.getRoles().stream()
                .map(Role::getName).sorted()
                .collect(Collectors.toList());
    }

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @NotBlank
    private String email;

    @NotNull
    @NotEmpty
    private List<String> roles;

    public UserDTO() {
    }

    public UserDTO(User user, long id, String name, String lastname, String email, List<String> roles) {
        this.user = user;
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
