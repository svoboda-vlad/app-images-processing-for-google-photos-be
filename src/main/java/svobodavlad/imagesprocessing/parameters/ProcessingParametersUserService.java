package svobodavlad.imagesprocessing.parameters;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessingParametersUserService {
	
	private final ProcessingParametersUserRepository parametersRepository;
	private final ProcessingParametersDefaultRepository parametersDefaultRepository;
	private final UserRepository userRepository;
	
	public ProcessingParametersUser resetToDefault() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
		List<ProcessingParametersDefault> parametersList = parametersDefaultRepository.findAll();
		if (parametersList.isEmpty()) throw new RuntimeException("Default parameters not found.");
		if (optParameters.isEmpty()) {
			ProcessingParametersUser parameters = parametersList.get(0).toProcessingParametersUser(optUser.get());
			return parametersRepository.save(parameters);
		}
		ProcessingParametersUser parameters = optParameters.get().resetToDefault(parametersList.get(0));
		return parametersRepository.save(parameters);
	}

}
