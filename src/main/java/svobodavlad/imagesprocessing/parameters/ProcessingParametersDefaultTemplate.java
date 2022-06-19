package svobodavlad.imagesprocessing.parameters;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;

@Data
@Accessors(chain = true)
public class ProcessingParametersDefaultTemplate {

	@NotNull
	@Min(60) @Max(86400)
	private int timeDiffGroup;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeWidth;
	
	@NotNull
	@Min(1) @Max(10000)
	private int resizeHeight;
	
	public ProcessingParametersDefault toProcessingParametersDefault(ProcessingParametersDefault parameters) {
		parameters.setTimeDiffGroup(timeDiffGroup);
		parameters.setResizeWidth(resizeWidth);
		parameters.setResizeHeight(resizeHeight);
		return parameters;
	}	

}