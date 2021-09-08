package svobodavlad.imagesprocessing.parameters;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import svobodavlad.imagesprocessing.security.User;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "processing_parameters_user", schema = "public") // needed for PostgreSQL
public class ProcessingParametersUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

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
	@NonNull	
	// fetch - changed to lazy
	// @JoinColumn(name = "user_id") - specified by default
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	public ProcessingParametersUserTemplate toProcessingParametersUserTemplate() {
		return new ProcessingParametersUserTemplate(this.getTimeDiffGroup(), this.getResizeWidth(), this.getResizeHeight());
	}

}