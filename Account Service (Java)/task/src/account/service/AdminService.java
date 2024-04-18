package account.service;

import account.dto.RoleDTO;
import account.dto.UserDTO;
import account.dto.UserDeleteResponse;
import account.dto.UserStateRequest;
import account.exception.CannotLockAdministratorException;
import account.exception.CannotRemoveAdministratorException;
import account.exception.CannotRemoveAdministratorRoleException;
import account.exception.RoleNotFoundException;
import account.exception.UserCannotCombineAdministrativeAndBusinessRoles;
import account.exception.UserDoesNotHaveSuchRoleException;
import account.exception.UserMustHaveAtLeastOneRoleException;
import account.exception.UserNotFoundException;
import account.mapper.UserMapper;
import account.model.Group;
import account.model.User;
import account.repository.GroupRepository;
import account.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final EventService eventService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final LoginAttemptService loginAttemptService;

    public AdminService(EventService eventService, UserRepository userRepository, GroupRepository groupRepository, LoginAttemptService loginAttemptService) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.loginAttemptService = loginAttemptService;
    }

    public List<UserDTO> getAllUsersOrderedById() {
        List<User> users = userRepository.findAllByOrderById();
        return users.stream().map(UserMapper::mapUserToUserDTO).toList();
    }

    public UserDeleteResponse deleteUserByEmail(String userEmail, String adminEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail)
                .orElseThrow(UserNotFoundException::new);

        List<String> roleCodes = user.getUserGroups().stream().map(Group::getCode).toList();

        if (roleCodes.contains("ROLE_ADMINISTRATOR")) {
            throw new CannotRemoveAdministratorException();
        }

        userRepository.delete(user);
        eventService.logDeleteUser(adminEmail, user.getEmail());

        return new UserDeleteResponse(userEmail, "Deleted successfully!");
    }

    public ResponseEntity<UserDTO> updateRoles(RoleDTO roleDTO, String adminEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(roleDTO.user())
                .orElseThrow(UserNotFoundException::new);

        Group roleSent = groupRepository.findByCode("ROLE_" + roleDTO.role())
                .orElseThrow(RoleNotFoundException::new);

        List<String> usersGroups = user.getUserGroups().stream().map(group -> group.getCode().substring(5)).toList();

        if (roleDTO.operation().equals("REMOVE")) {
            removeRoleFromUser(roleDTO.role(), user, roleSent);
            eventService.logRemoveRole(adminEmail, roleDTO.role(), user.getEmail());
        }

        if (roleDTO.operation().equals("GRANT")) {
            grantRoleToUser(roleDTO.role(), usersGroups, user, roleSent);

            eventService.logGrantRole(adminEmail, roleDTO.role(), user.getEmail());
        }

        return ResponseEntity.ok(UserMapper.mapUserToUserDTO(userRepository.save(user)));
    }

    private void removeRoleFromUser(String role, User user, Group roleSent) {
        if (!user.getUserGroups().contains(roleSent)) {
            throw new UserDoesNotHaveSuchRoleException();
        }

        if (role.equals("ADMINISTRATOR")) {
            throw new CannotRemoveAdministratorRoleException();
        }

        if (user.getUserGroups().size() == 1) {
            throw new UserMustHaveAtLeastOneRoleException();
        }

        user.removeUserGroup(roleSent);
    }

    private void grantRoleToUser(String role, List<String> roleList, User user, Group roleSent) {
        List<String> businessRoles = List.of("USER", "ACCOUNTANT", "AUDITOR");
        String adminRole = "ADMINISTRATOR";

        boolean hasConflictingRoles = (roleList.stream().anyMatch(businessRoles::contains) && role.equals(adminRole)) ||
                                      (roleList.contains(adminRole) && businessRoles.contains(role));

        if (hasConflictingRoles) {
            throw new UserCannotCombineAdministrativeAndBusinessRoles();
        }

        user.getUserGroups().add(roleSent);
    }

    public ResponseEntity<?> manageUserState(UserStateRequest userStateRequest, String adminEmail) {
        String userEmail = userStateRequest.user().toLowerCase();
        String status = "";
        User user = userRepository.findUserByEmailIgnoreCase(userEmail)
                .orElseThrow(UserNotFoundException::new);

        if (user.isAdmin()) throw new CannotLockAdministratorException();

        if (userStateRequest.operation().equals("LOCK")) {
            user.setNonLocked(false);
            eventService.logLockUser(userEmail);
            status = "User " + userEmail + " locked!";
        }

        if (userStateRequest.operation().equals("UNLOCK")) {
            user.setNonLocked(true);
            eventService.logUnlockUser(adminEmail, userEmail);
            status = "User " + userEmail + " unlocked!";
            loginAttemptService.cleanAttempts(userEmail);
        }

        return ResponseEntity.ok(Map.of(
                "status", status
        ));
    }
}
