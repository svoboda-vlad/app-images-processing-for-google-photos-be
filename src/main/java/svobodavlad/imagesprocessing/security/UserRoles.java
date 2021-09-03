package svobodavlad.imagesprocessing.security;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_roles", schema = "public") // needed for PostgreSQL
@IdClass(UserRolesId.class)
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
