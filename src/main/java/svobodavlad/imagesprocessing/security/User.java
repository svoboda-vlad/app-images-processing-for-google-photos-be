package svobodavlad.imagesprocessing.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", schema = "public") // needed for PostgreSQL
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @NotNull
    @Size(min = 1, max = 255)
	private String username;

    @NotNull
    @Size(min = 60, max = 60)
    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;    
    
    @NotNull
    @Size(min = 1, max = 255)
	private String givenName;

    @NotNull
    @Size(min = 1, max = 255)
	private String familyName;
    
    private LocalDateTime lastLoginDateTime;
    private LocalDateTime previousLoginDateTime;
    
    // CascadeType.ALL - enable removing the relation (user_roles.user_id)
    // orphanRemoval - enable removing the related entity (user_roles)
    // fetch - changed to eager
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<UserRoles> roles = new ArrayList<UserRoles>();
    
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