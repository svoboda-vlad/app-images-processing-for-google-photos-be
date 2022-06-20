package svobodavlad.imagesprocessing.jpaentities;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import svobodavlad.imagesprocessing.security.UserTemplate;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true) // roles excluded to avoid circular dependency
@NoArgsConstructor
@Accessors(chain = true)
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

	public UserTemplate toUserTemplate() {
		return new UserTemplate()
				.setUsername(this.getUsername())
				.setGivenName(this.getGivenName())
				.setFamilyName(this.getFamilyName())
				.setEmail(this.getEmail());
	}
}