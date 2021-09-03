package svobodavlad.imagesprocessing.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
class LoginUser {

	@NonNull
	private String username;
	
	@NonNull
	private String password;

}