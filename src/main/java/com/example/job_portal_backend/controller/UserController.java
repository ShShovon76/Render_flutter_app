package com.example.job_portal_backend.controller;

import com.example.job_portal_backend.dtos.auth.ChangePasswordRequest;
import com.example.job_portal_backend.dtos.user.UpdateUserRequest;
import com.example.job_portal_backend.dtos.user.UserDto;

import com.example.job_portal_backend.enums.UserRole;
import com.example.job_portal_backend.service.FileStorageService;
import com.example.job_portal_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean enabled,
            Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(keyword, role, enabled, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {

        userService.changePassword(id,
                request.getCurrentPassword(),
                request.getNewPassword());
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableUser(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        userService.enableUser(id, enabled);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count-by-role")
    public ResponseEntity<Long> countUsersByRole(@RequestParam UserRole role) {
        return ResponseEntity.ok(userService.countUsersByRole(role));
    }
    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<UserDto> uploadProfilePicture(
            @PathVariable Long id,
            @RequestParam MultipartFile file) throws Exception {

        String path = fileStorageService.storeProfilePicture(file);
        String url = fileStorageService.getFileUrl(path);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .profilePictureUrl(url)
                .build();

        return ResponseEntity.ok(userService.updateUser(id, request));
    }
}
