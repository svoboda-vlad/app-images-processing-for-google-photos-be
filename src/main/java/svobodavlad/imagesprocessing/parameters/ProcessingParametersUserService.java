package svobodavlad.imagesprocessing.parameters;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;
import svobodavlad.imagesprocessing.security.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessingParametersUserService {
	
	private final ProcessingParametersUserRepository parametersRepository;
	private final ProcessingParametersDefaultRepository parametersDefaultRepository;
	private final UserRepository userRepository;
	
	public ProcessingParametersUser resetToDefault() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var optUser = userRepository.findByUsername(authentication.getName());
		var optParameters = parametersRepository.findByUser(optUser.get());
		var parametersDefaultList = parametersDefaultRepository.findAll();
		if (parametersDefaultList.isEmpty()) throw new RuntimeException("Default parameters not found.");
		if (optParameters.isEmpty()) {
			var parameters = parametersDefaultList.get(0).toProcessingParametersUser(optUser.get());
			return parametersRepository.save(parameters);
		}
		var parameters = optParameters.get().resetToDefault(parametersDefaultList.get(0));
		return parametersRepository.save(parameters);
	}
	
	public Optional<ProcessingParametersUser> setInitialParameters(String username) {
		var optUser = userRepository.findByUsername(username);
		var optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) {
			var parametersDefaultList = parametersDefaultRepository.findAll();
			if (parametersDefaultList.isEmpty()) throw new RuntimeException("Default parameters not found.");
			var parameters = parametersDefaultList.get(0).toProcessingParametersUser(optUser.get());
			return Optional.of(parametersRepository.save(parameters));
		}
		return Optional.empty();
	}
	
	public void deleteForCurrentUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var optUser = userRepository.findByUsername(authentication.getName());
		optUser.ifPresent(user -> {
			var optParameters = parametersRepository.findByUser(user);
			optParameters.ifPresent(parameters -> {
				parametersRepository.delete(parameters);
				parametersRepository.flush();
			});
		});
	}
	
	public Optional<ProcessingParametersUser> getForCurrentUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		var optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) return Optional.empty();
		return optParameters;
	}
	
	public Optional<ProcessingParametersUser> updateForCurrentUser(ProcessingParametersUserTemplate parametersTemplate) {		
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		var optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) return Optional.empty();
		var parameters = optParameters.get().updateFromTemplate(parametersTemplate);
		return Optional.of(parametersRepository.save(parameters));
	}

}
