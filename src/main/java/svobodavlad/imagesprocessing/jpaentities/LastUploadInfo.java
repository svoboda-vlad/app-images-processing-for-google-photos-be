package svobodavlad.imagesprocessing.jpaentities;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import svobodavlad.imagesprocessing.lastupload.LastUploadInfoTemplate;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
public class LastUploadInfo extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

	private Instant lastUploadDateTime;
	
	@NotNull
	// fetch - changed to lazy
	// @JoinColumn(name = "user_id") - specified by default
	@OneToOne(fetch = FetchType.LAZY)
	private User user;
	
	public LastUploadInfo updateLastUploadDateTime(Instant currentDateTime) {
		if (currentDateTime == null) currentDateTime = Instant.now();
		this.lastUploadDateTime = currentDateTime;
		return this;
	}
	
	public LastUploadInfoTemplate toLastUploadInfoTemplate() {
		return new LastUploadInfoTemplate().setLastUploadDateTime(this.lastUploadDateTime);
	}	

}