package svobodavlad.imagesprocessing.jpaentities;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "last_upload_info", schema = "public") // needed for PostgreSQL
@Getter @Setter @ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LastUploadInfo extends JpaEntityTemplate {
	
	private static final long serialVersionUID = 1L;

	private Instant lastUploadDateTime;
	
	@NotNull
	// fetch - changed to lazy
	// @JoinColumn(name = "user_id") - specified by default
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	public LastUploadInfo updateLastUploadDateTime() {
		this.lastUploadDateTime = Instant.now();
		return this;
	}	

}