package account.mapper;

import account.dto.UserDTO;
import account.model.Group;
import account.model.User;

import java.util.List;

public class UserMapper {
    public static UserDTO mapUserToUserDTO(User user) {
        List<String> roleCodes = user.getUserGroups().stream().map(Group::getCode).sorted().toList();
        return new UserDTO(user.getId(), user.getName(), user.getLastname(), user.getEmail(), roleCodes);
    }

    public User mapUserDTOToUser(UserDTO userDTO) {
        return new User();
    }
}
