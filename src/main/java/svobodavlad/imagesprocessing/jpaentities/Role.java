package svobodavlad.imagesprocessing.jpaentities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "role", schema = "public") // needed for PostgreSQL
@Getter @Setter @ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Role extends JpaEntityTemplate implements GrantedAuthority {
	
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Override
	@JsonIgnore
	public String getAuthority() {
		return this.name;
	}

}
