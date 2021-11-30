package svobodavlad.imagesprocessing.jpaentities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import svobodavlad.imagesprocessing.security.UserRolesId;

@Entity
@Table(name = "user_roles", schema = "public") // needed for PostgreSQL
@IdClass(UserRolesId.class)
@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRoles implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	// lazy fetching disabled - primary key
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "user_id") - specified by default
	@ManyToOne
	@JsonIgnore // to avoid infinite recursion
	private User user;

	@Id
	// lazy fetching disabled - primary key
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "role_id") - specified by default	
	@ManyToOne
	private Role role;

}
