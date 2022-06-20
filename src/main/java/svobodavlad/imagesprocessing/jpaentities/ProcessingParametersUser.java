package svobodavlad.imagesprocessing.jpaentities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserTemplate;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
public class ProcessingParametersUser extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

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
		return new ProcessingParametersUserTemplate()
				.setTimeDiffGroup(this.getTimeDiffGroup())
				.setResizeWidth(this.getResizeWidth())
				.setResizeHeight(this.getResizeHeight());
	}
	
	public ProcessingParametersUser resetToDefault(ProcessingParametersDefault parametersDefault) {
		this.setTimeDiffGroup(parametersDefault.getTimeDiffGroup());
		this.setResizeHeight(parametersDefault.getResizeHeight());
		this.setResizeWidth(parametersDefault.getResizeWidth());
		return this;
	}
	
	public ProcessingParametersUser updateFromTemplate(ProcessingParametersUserTemplate template) {
		this.setTimeDiffGroup(template.getTimeDiffGroup());
		this.setResizeHeight(template.getResizeHeight());
		this.setResizeWidth(template.getResizeWidth());
		return this;
	}

}