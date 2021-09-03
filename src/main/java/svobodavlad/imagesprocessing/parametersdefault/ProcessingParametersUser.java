package svobodavlad.imagesprocessing.parametersdefault;

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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.security.User;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "processing_parameters_user", schema = "public") // needed for PostgreSQL
public class ProcessingParametersUser implements Serializable {

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
	
	// fetch - changed to lazy
	// @JoinColumn(name = "user_id") - specified by default
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;

}