package svobodavlad.imagesprocessing.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class LoginUser {

	private String username;	
	private String password;

}