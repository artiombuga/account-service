package account.controller;

import account.dto.RoleDTO;
import account.dto.UserDTO;
import account.dto.UserStateRequest;
import account.model.User;
import account.security.UserAdapter;
import account.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @GetMapping("/user/")
    public ResponseEntity<List<UserDTO>> getUser() {
        return ResponseEntity.ok(service.getAllUsersOrderedById());
    }

    @DeleteMapping("/user/{userEmail}")
    public ResponseEntity<?> deleteUser(@PathVariable String userEmail,
                                        @AuthenticationPrincipal UserAdapter loggedUser) {
        return ResponseEntity.ok(service.deleteUserByEmail(userEmail, loggedUser.getUsername()));
    }

    @PutMapping("user/role")
    public ResponseEntity<UserDTO> changeUserRoles(@Valid @RequestBody RoleDTO roleDTO,
                                                   @AuthenticationPrincipal UserAdapter loggedUser) {
        return service.updateRoles(roleDTO, loggedUser.getUsername());
    }

    @PutMapping("user/access")
    public ResponseEntity<?> manageUserState(@Valid @RequestBody UserStateRequest userStateRequest,
                                             @AuthenticationPrincipal UserAdapter loggedUser) {
        return service.manageUserState(userStateRequest, loggedUser.getUsername());
    }

}
