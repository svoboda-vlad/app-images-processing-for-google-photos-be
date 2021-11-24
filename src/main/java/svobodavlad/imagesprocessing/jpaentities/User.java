package svobodavlad.imagesprocessing.jpaentities;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import svobodavlad.imagesprocessing.security.UserInfo;

@Entity
@Table(name = "user", schema = "public") // needed for PostgreSQL
@Getter @Setter @ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "parameters"}) // roles excluded to avoid circular dependency
@NoArgsConstructor
@RequiredArgsConstructor
public class User extends JpaEntityTemplate implements UserDetails {
	
	private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 255)
	@NonNull private String username;

    @NotNull
    @Size(min = 60, max = 60)
    @JsonIgnore
    @NonNull private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @NonNull private LoginProvider loginProvider;    
    
    @NotNull
    @Size(min = 1, max = 255)
    @NonNull private String givenName;

    @NotNull
    @Size(min = 1, max = 255)
    @NonNull private String familyName;
    
    private LocalDateTime lastLoginDateTime;
    private LocalDateTime previousLoginDateTime;
    
    // CascadeType.ALL - enable insert, select, update and delete of user roles using user entity
    // orphanRemoval - enable delete of user roles with user set to null
    // fetch - changed to eager
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public Set<UserRoles> roles = new HashSet<UserRoles>();
    
    // CascadeType.ALL - enable insert, select, update and delete of processing parameters user using user entity
    // orphanRemoval - enable delete of processing parameters user with user set to null
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
	public Set<ProcessingParametersUser> parameters = new HashSet<ProcessingParametersUser>();    
    
	public void addRole(Role role) {
		UserRoles userRoles = new UserRoles(this,role);
		roles.add(userRoles);
		// role.getUsers().add(userRoles);
	}

	public void removeRole(Role role) {
		UserRoles userRoles = new UserRoles(this,role);
		// role.getUsers().remove(userRoles);
		roles.remove(userRoles);
		// @NotNull applied on user + role
		// userRoles.setUser(null);
		// userRoles.setRole(null);
	}    

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (UserRoles role : this.roles) {
        	authorities.add(new SimpleGrantedAuthority(role.getRole().getName()));
        }
        return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public void updateLastLoginDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		if (this.lastLoginDateTime == null) {
			this.previousLoginDateTime = currentDateTime;
		} else {
			this.previousLoginDateTime = this.lastLoginDateTime;
		}
		this.lastLoginDateTime = currentDateTime;
	}
	
	public enum LoginProvider {	
		INTERNAL,
		GOOGLE
	}
	
	public UserInfo toUserInfo() {
		UserInfo userInfo = new UserInfo(this.getUsername(), this.getGivenName(), this.getFamilyName(), this.getLastLoginDateTime(), this.getPreviousLoginDateTime(), this.getRoles());
		return userInfo;
	}
	
}