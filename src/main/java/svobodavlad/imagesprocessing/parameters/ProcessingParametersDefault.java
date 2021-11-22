package svobodavlad.imagesprocessing.parameters;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import svobodavlad.imagesprocessing.jpautil.JpaEntityTemplate;
import svobodavlad.imagesprocessing.security.User;

@Entity
@Table(name = "processing_parameters_default", schema = "public") // needed for PostgreSQL
@Getter @Setter @ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingParametersDefault extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

	@Min(60) @Max(86400)
	private int timeDiffGroup;
	
	@Min(1) @Max(10000)
	private int resizeWidth;
	
	@Min(1) @Max(10000)
	private int resizeHeight;
	
	public ProcessingParametersDefaultTemplate toProcessingParametersDefaultTemplate() {
		return new ProcessingParametersDefaultTemplate(this.getTimeDiffGroup(), this.getResizeWidth(), this.getResizeHeight());
	}
	
	public ProcessingParametersUser toProcessingParametersUser(User user) {
		ProcessingParametersUser parameters = new ProcessingParametersUser(timeDiffGroup, resizeWidth, resizeHeight);
		parameters.setUser(user);
		return parameters;
	}

}