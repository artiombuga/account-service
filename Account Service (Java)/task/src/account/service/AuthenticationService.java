package account.service;

import account.dto.ChangePasswordRequest;
import account.dto.ChangePasswordResponse;
import account.dto.UserDTO;
import account.exception.BreachedPasswordException;
import account.exception.RoleNotFoundException;
import account.exception.SamePasswordException;
import account.exception.UserExistsException;
import account.mapper.UserMapper;
import account.model.Group;
import account.model.User;
import account.repository.GroupRepository;
import account.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EventService eventService;

    public AuthenticationService(PasswordEncoder passwordEncoder, UserRepository userRepository, GroupRepository groupRepository, EventService eventService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.eventService = eventService;
    }

    public UserDTO registerUser(User user) {
        user.setEmail(user.getEmail().toLowerCase());

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new UserExistsException();
        }

        if (isBreached(user.getPassword())) {
            throw new BreachedPasswordException();
        }

        User preparedUser = new User();
        preparedUser.setEmail(user.getEmail());
        preparedUser.setName(user.getName());
        preparedUser.setLastname(user.getLastname());
        preparedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        updateUserGroup(preparedUser);
        User savedUser = userRepository.save(preparedUser);

        eventService.logCreateUser(user.getEmail());
        return UserMapper.mapUserToUserDTO(savedUser);
    }

    private void updateUserGroup(User user) {
        Group userGroup;
        if (userRepository.count() == 0) {
            userGroup = groupRepository.findByCode("ROLE_ADMINISTRATOR")
                    .orElseThrow(RoleNotFoundException::new);
        } else {
            userGroup = groupRepository.findByCode("ROLE_USER")
                    .orElseThrow(RoleNotFoundException::new);
        }
        user.addUserGroup(userGroup);
    }


    public ChangePasswordResponse changePassword(String username, ChangePasswordRequest ChangePasswordRequest) {
        if (isBreached(ChangePasswordRequest.newPassword())) {
            throw new BreachedPasswordException();
        }

        if (isPasswordTheSame(ChangePasswordRequest.newPassword(), username)) {
            throw new SamePasswordException();
        }

        User user = userRepository.findUserByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        user.setPassword(passwordEncoder.encode(ChangePasswordRequest.newPassword()));
        userRepository.save(user);
        eventService.logChangePassword(user.getEmail());

        return new ChangePasswordResponse(username, "The password has been updated successfully");
    }

    private boolean isBreached(String newPassword) {
        List<String> breachedPasswords = List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

        return breachedPasswords.contains(newPassword);
    }

    private boolean isPasswordTheSame(String newPassword, String username) {
        User user = userRepository.findUserByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        String oldPassword = user.getPassword();

        return passwordEncoder.matches(newPassword, oldPassword);
    }
}
