package svobodavlad.imagesprocessing.jpaentities;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultTemplate;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
public class ProcessingParametersDefault extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

	@Min(60) @Max(86400)
	private int timeDiffGroup;
	
	@Min(1) @Max(10000)
	private int resizeWidth;
	
	@Min(1) @Max(10000)
	private int resizeHeight;
	
	public ProcessingParametersDefaultTemplate toProcessingParametersDefaultTemplate() {
		return new ProcessingParametersDefaultTemplate()
				.setTimeDiffGroup(this.getTimeDiffGroup())
				.setResizeWidth(this.getResizeWidth())
				.setResizeHeight(this.getResizeHeight());
	}
	
	public ProcessingParametersUser toProcessingParametersUser(User user) {
		return new ProcessingParametersUser()
				.setTimeDiffGroup(timeDiffGroup)
				.setResizeWidth(resizeWidth)
				.setResizeHeight(resizeHeight)
				.setUser(user);
	}

}