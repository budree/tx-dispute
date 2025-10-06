package com.example.txd.controller.api.v1;

import com.example.txd.model.AppUser;
import com.example.txd.model.Role;
import com.example.txd.repo.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Users")
@RestController @RequestMapping("/api/v1/users")
public class UsersApi {
    record CreateUserRequest(@NotBlank String username, @NotBlank String password, Role role) {}
    record UserResponse(Long id, String username, Role role) {}

    private final UserRepository users; private final PasswordEncoder enc;
    public UsersApi(UserRepository users, PasswordEncoder enc){ this.users=users; this.enc=enc; }

    @GetMapping public List<UserResponse> list() {
        return users.findAll().stream().map(u -> new UserResponse(u.getId(), u.getUsername(), u.getRole())).toList();
    }

    @PostMapping public UserResponse create(@RequestBody CreateUserRequest req) {
        var u = new AppUser();
        u.setUsername(req.username());
        u.setPasswordHash(enc.encode(req.password()));
        u.setRole(req.role()==null? Role.CLIENT : req.role());
        u = users.save(u);
        return new UserResponse(u.getId(), u.getUsername(), u.getRole());
    }
}
