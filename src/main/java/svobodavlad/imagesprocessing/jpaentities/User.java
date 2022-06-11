package svobodavlad.imagesprocessing.jpaentities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@EqualsAndHashCode(callSuper = true, exclude = "roles") // roles excluded to avoid circular dependency
@NoArgsConstructor
@RequiredArgsConstructor
public class User extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 255)
	@NonNull
	private String username;

	@NotNull
	@Size(min = 1, max = 255)
	@NonNull
	private String givenName;

	@NotNull
	@Size(min = 1, max = 255)
	@NonNull
	private String familyName;
	
	@Size(min = 1, max = 255)
	private String email;	

	private Instant lastLoginDateTime;
	private Instant previousLoginDateTime;

	// CascadeType.MERGE, PERSIST - enable insert, select, update of user roles
	// using user entity
	// fetch - changed to eager
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("role") // to keep order of elements fixed in JSON output
	public List<UserRoles> roles = new ArrayList<UserRoles>();

	public void addRole(Role role) {
		UserRoles userRoles = new UserRoles(this, role);
		roles.add(userRoles);
		// role.getUsers().add(userRoles);
	}

	public void removeRole(Role role) {
		UserRoles userRoles = new UserRoles(this, role);
		// role.getUsers().remove(userRoles);
		roles.remove(userRoles);
		// @NotNull applied on user + role
		// userRoles.setUser(null);
		// userRoles.setRole(null);
	}

	public void updateLastLoginDateTime(Instant currentDateTime) {
		if (currentDateTime == null) currentDateTime = Instant.now();
		if (this.lastLoginDateTime == null) {
			this.previousLoginDateTime = currentDateTime;
		} else {
			this.previousLoginDateTime = this.lastLoginDateTime;
		}
		this.lastLoginDateTime = currentDateTime;
	}

	public UserInfo toUserInfo() {
		UserInfo userInfo = new UserInfo(this.getUsername(), this.getGivenName(), this.getFamilyName(), this.getEmail(),
				this.getLastLoginDateTime(), this.getPreviousLoginDateTime(), this.getRoles());
		return userInfo;
	}
}