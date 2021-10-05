package svobodavlad.imagesprocessing.parameters;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import svobodavlad.imagesprocessing.jpautil.JpaEntityTemplate;
import svobodavlad.imagesprocessing.security.User;

@Entity
@Table(name = "processing_parameters_user", schema = "public") // needed for PostgreSQL
@Getter @Setter @ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingParametersUser extends JpaEntityTemplate {

	@NotNull
	@Min(60) @Max(86400)
	private int timeDiffGroup;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeWidth;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeHeight;
	
	@NotNull
	// fetch - changed to lazy
	// @JoinColumn(name = "user_id") - specified by default
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	public ProcessingParametersUserTemplate toProcessingParametersUserTemplate() {
		return new ProcessingParametersUserTemplate(this.getTimeDiffGroup(), this.getResizeWidth(), this.getResizeHeight());
	}
	
	public ProcessingParametersUser resetToDefault(ProcessingParametersDefault parametersDefault) {
		this.setTimeDiffGroup(parametersDefault.getTimeDiffGroup());
		this.setResizeHeight(parametersDefault.getResizeHeight());
		this.setResizeWidth(parametersDefault.getResizeWidth());
		return this;
	}

}