package svobodavlad.imagesprocessing.google;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleIdTokenTemplate {

	@NotNull
	@Size(min = 1, max = 2048)
	private String idToken;

}
