package com.shortthirdman.bootlabs.jwtauth.refreshtoken.service;

import com.shortthirdman.bootlabs.jwtauth.refreshtoken.common.UserAuthException;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.common.UserRoleNotFoundException;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.configuration.UserInfoDetails;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.configuration.jwt.JwtUtils;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.request.LoginRequest;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.request.SignupRequest;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.response.JwtResponse;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.response.MessageResponse;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.RefreshToken;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.Role;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.RoleType;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.User;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.repository.RoleRepository;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public MessageResponse createNewUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAuthException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAuthException("Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                    .orElseThrow(() -> new UserRoleNotFoundException("USER: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new UserRoleNotFoundException("ADMIN: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(RoleType.ROLE_MODERATOR)
                                .orElseThrow(() -> new UserRoleNotFoundException("MODERATOR: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                                .orElseThrow(() -> new UserRoleNotFoundException("USER: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User created successfully!");
    }

    public JwtResponse userLogin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles);
    }
}
