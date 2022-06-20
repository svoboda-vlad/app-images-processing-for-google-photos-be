package svobodavlad.imagesprocessing.lastupload;

import java.time.Instant;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LastUploadInfoTemplate {
	
	private Instant lastUploadDateTime;

}
