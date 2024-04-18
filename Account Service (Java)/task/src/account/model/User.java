package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "dbo_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotBlank
    private String lastname;

    @Column(nullable = false)
    @Pattern(regexp = ".*@acme\\.com")
    @NotBlank
    private String email;

    @Column(nullable = false)
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @NotBlank
    private String password;

    @JsonIgnore
    private boolean isNonLocked = true;

    @Column(nullable = false)
    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @ToString.Exclude
    private List<Group> userGroups = new ArrayList<>();

    public void addUserGroup(Group userGroup) {
        userGroups.add(userGroup);
    }

    public void removeUserGroup(Group userGroup) {
        userGroups.remove(userGroup);
    }

    public boolean isAdmin() {
        return userGroups.stream().map(Group::getCode).toList().contains("ROLE_ADMINISTRATOR");
    }
}
