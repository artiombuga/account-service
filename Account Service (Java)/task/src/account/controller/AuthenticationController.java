package account.controller;

import account.dto.ChangePasswordRequest;
import account.dto.UserDTO;
import account.dto.CustomErrorResponse;
import account.model.User;
import account.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.service = authenticationService;
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(service.registerUser(user));
    }

    @PostMapping(value = "/changepass")
    public ResponseEntity<Object> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody ChangePasswordRequest newPassword) {
        return ResponseEntity.ok(service.changePassword(userDetails.getUsername().toLowerCase(), newPassword));
    }
}

