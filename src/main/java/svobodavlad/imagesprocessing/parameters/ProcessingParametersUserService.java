package svobodavlad.imagesprocessing.parameters;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;
import svobodavlad.imagesprocessing.jpaentities.User;
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
		List<ProcessingParametersDefault> parametersDefaultList = parametersDefaultRepository.findAll();
		if (parametersDefaultList.isEmpty()) throw new RuntimeException("Default parameters not found.");
		if (optParameters.isEmpty()) {
			ProcessingParametersUser parameters = parametersDefaultList.get(0).toProcessingParametersUser(optUser.get());
			return parametersRepository.save(parameters);
		}
		ProcessingParametersUser parameters = optParameters.get().resetToDefault(parametersDefaultList.get(0));
		return parametersRepository.save(parameters);
	}
	
	public ProcessingParametersUser setInitialParameters(String username) {
		Optional<User> optUser = userRepository.findByUsername(username);
		Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) {
			List<ProcessingParametersDefault> parametersDefaultList = parametersDefaultRepository.findAll();
			if (parametersDefaultList.isEmpty()) throw new RuntimeException("Default parameters not found.");
			ProcessingParametersUser parameters = parametersDefaultList.get(0).toProcessingParametersUser(optUser.get());
			return parametersRepository.save(parameters);
		}
		return null;
	}
	
	public void deleteForCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isPresent()) {
			Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
			if (optParameters.isPresent()) {
				parametersRepository.delete(optParameters.get());
				parametersRepository.flush();
			}
		}
	}
	
	public Optional<ProcessingParametersUser> getForCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) return Optional.empty();
		return optParameters;
	}
	
	public ProcessingParametersUser updateForCurrentUser(ProcessingParametersUserTemplate parametersTemplate) {		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return null;
		Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) return null;
		ProcessingParametersUser parameters = optParameters.get().updateFromTemplate(parametersTemplate);
		return parametersRepository.save(parameters);
	}	

}
