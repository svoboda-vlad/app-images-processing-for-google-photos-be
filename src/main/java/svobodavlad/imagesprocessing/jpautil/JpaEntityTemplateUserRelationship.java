package svobodavlad.imagesprocessing.jpautil;

import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import svobodavlad.imagesprocessing.security.User;

@MappedSuperclass
@Getter @Setter
public abstract class JpaEntityTemplateUserRelationship extends JpaEntityTemplate {

	@NotNull
	// fetch - changed to lazy
	// @JoinColumn(name = "user_id") - specified by default
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

}