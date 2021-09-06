package svobodavlad.imagesprocessing.parameters;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "processing_parameters_default", schema = "public") // needed for PostgreSQL
public class ProcessingParametersDefault implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@NonNull
	@Min(60) @Max(86400)
	private int timeDiffGroup;
	
	@NotNull
	@NonNull
	@Min(1) @Max(10000)
	private int resizeWidth;
	
	@NotNull
	@NonNull
	@Min(1) @Max(10000)
	private int resizeHeight;
	
	public ProcessingParametersDefaultTemplate toProcessingParametersDefaultTemplate() {
		return new ProcessingParametersDefaultTemplate(this.getTimeDiffGroup(), this.getResizeWidth(), this.getResizeHeight());
	}	

}