package svobodavlad.imagesprocessing.jpautil;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import svobodavlad.imagesprocessing.security.UserRoles;

@MappedSuperclass
@Getter @Setter @ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public abstract class JpaEntityTemplateUserRolesRelationship extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

    // CascadeType.ALL - enable removing the relation (user_roles.user_id)
    // orphanRemoval - enable removing the related entity (user_roles)
    // fetch - changed to eager
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<UserRoles> roles = new ArrayList<UserRoles>();    

}