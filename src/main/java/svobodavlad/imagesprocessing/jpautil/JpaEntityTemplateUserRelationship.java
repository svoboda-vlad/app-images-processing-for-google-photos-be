package svobodavlad.imagesprocessing.jpautil;

import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import svobodavlad.imagesprocessing.security.User;

@MappedSuperclass
@Getter @Setter @ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public abstract class JpaEntityTemplateUserRelationship extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

	@NotNull
	// fetch - changed to lazy
	// @JoinColumn(name = "user_id") - specified by default
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

}