package svobodavlad.imagesprocessing.parameters;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import svobodavlad.imagesprocessing.jpautil.JpaEntityTemplateUserRelationship;

@Entity
@Table(name = "processing_parameters_user", schema = "public") // needed for PostgreSQL
@Getter @Setter @ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingParametersUser extends JpaEntityTemplateUserRelationship {

	@NotNull
	@Min(60) @Max(86400)
	private int timeDiffGroup;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeWidth;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeHeight;
	
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