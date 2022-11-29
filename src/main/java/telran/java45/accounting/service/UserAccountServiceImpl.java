package telran.java45.accounting.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java45.accounting.dao.UserAccountRepository;
import telran.java45.accounting.dto.RolesResponseDto;
import telran.java45.accounting.dto.UserAccountResponceDto;
import telran.java45.accounting.dto.UserRegisterDto;
import telran.java45.accounting.dto.UserUpdateDto;
import telran.java45.accounting.dto.exception.UserExistsException;
import telran.java45.accounting.dto.exception.UserNotExistsException;
import telran.java45.accounting.dto.exception.UserNotFoundException;
import telran.java45.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
	
	final UserAccountRepository repository;
	final ModelMapper modelMapper;
	
	@Override
	public UserAccountResponceDto addUser(UserRegisterDto userRegisterDto) throws Exception {
		if (repository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistsException(userRegisterDto.getLogin());
		}
		UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
		repository.save(userAccount);
		return modelMapper.map(userAccount, UserAccountResponceDto.class);
	}

	@Override
	public UserAccountResponceDto getUser(String login) {
		UserAccount userAccount = repository.findById(login).orElseThrow(UserNotFoundException::new);
		return modelMapper.map(userAccount, UserAccountResponceDto.class);
	}

	@Override
	public UserAccountResponceDto removeUser(String login) {
		if (!repository.existsById(login)) {
			throw new UserNotExistsException(login);
		}
		UserAccount userAccount = repository.findById(login).orElseThrow(UserNotFoundException::new);
		repository.delete(userAccount);
		return modelMapper.map(userAccount, UserAccountResponceDto.class);
	}

	@Override
	public UserAccountResponceDto editUser(String login, UserUpdateDto updateDto) {
		if (!repository.existsById(login)) {
			throw new UserNotExistsException(login);
		}
		UserAccount userAccount = repository.findById(login).orElseThrow(UserNotFoundException::new);
		if (updateDto.getFirstName() != null) {
			userAccount.setFirstName(updateDto.getFirstName());
		}
		if (updateDto.getLastName() != null) {
			userAccount.setLastName(updateDto.getLastName());
		}
		repository.save(userAccount);
		return modelMapper.map(userAccount, UserAccountResponceDto.class);
	}

	@Override
	public RolesResponseDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount userAccount = repository.findById(login).orElseThrow(UserNotFoundException::new);
		if (isAddRole) {
			userAccount.addRole(role);
		} else {
			userAccount.removeRole(role);
		}
		return modelMapper.map(userAccount, RolesResponseDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = repository.findById(login).orElseThrow(UserNotFoundException::new);
		userAccount.setPassword(newPassword);

	}

}
