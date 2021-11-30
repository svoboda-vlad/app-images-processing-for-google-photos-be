package svobodavlad.imagesprocessing.parameters;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;

@Data
@AllArgsConstructor
public class ProcessingParametersUserTemplate {

	@NotNull
	@Min(60) @Max(86400)
	private int timeDiffGroup;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeWidth;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeHeight;
	
	public ProcessingParametersUser toProcessingParametersUser(ProcessingParametersUser parameters) {
		parameters.setTimeDiffGroup(timeDiffGroup);
		parameters.setResizeWidth(resizeWidth);
		parameters.setResizeHeight(resizeHeight);
		return parameters;
	}
}