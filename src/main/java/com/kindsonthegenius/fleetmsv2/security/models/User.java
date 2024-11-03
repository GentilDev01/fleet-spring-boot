package com.kindsonthegenius.fleetmsv2.security.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "\"User\"")
public class User extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private boolean accountVerified;
    private boolean loginDisabled;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<SecureToken> tokens;
      
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Getters and setters
    public Integer getId() { return Id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public boolean isAccountVerified() { return accountVerified; }
    public void setAccountVerified(boolean verified) { this.accountVerified = verified; }
    public void setPassword(String password) { this.password = password; }
    public Set<SecureToken> getTokens() { return tokens; }
    public void setTokens(Set<SecureToken> tokens) { this.tokens = tokens; }
}
