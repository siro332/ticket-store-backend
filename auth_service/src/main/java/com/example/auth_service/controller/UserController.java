package com.example.auth_service.controller;

import com.example.auth_service.dto.JwtResponse;
import com.example.auth_service.model.User;
import com.example.auth_service.model.UserOrganizationRole;
import com.example.auth_service.repository.UserOrganizationRoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserOrganizationRoleRepository userOrganizationRoleRepository;
    private final OrganizationService organizationService;

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserOrganizationRole> userOrgRoles = userOrganizationRoleRepository.findByUserId(user.getId());

        Set<String> allRoles = new HashSet<>();
        Set<String> allPermissions = new HashSet<>();
        List<JwtResponse.UserOrganizationRoleDto> orgRoleDtos = userOrgRoles.stream().map(uor -> {
            allRoles.add(uor.getRole().getName()); // Add role name to global roles
            Set<String> rolePermissions = uor.getRole().getPermissions().stream()
                    .map(permission -> permission.getName())
                    .collect(Collectors.toSet());
            allPermissions.addAll(rolePermissions); // Add permissions to global permissions

            return JwtResponse.UserOrganizationRoleDto.builder()
                    .organizationId(uor.getOrganization().getId())
                    .organizationName(uor.getOrganization().getName())
                    .roleName(uor.getRole().getName())
                    .permissions(new java.util.ArrayList<>(rolePermissions))
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "roles", allRoles, // Aggregated roles
                "permissions", allPermissions, // Aggregated permissions
                "organizationRoles", orgRoleDtos
        ));
    }
}
