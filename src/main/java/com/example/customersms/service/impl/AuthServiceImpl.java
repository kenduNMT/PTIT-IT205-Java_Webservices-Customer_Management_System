package com.example.customersms.service.impl;

import com.example.customersms.config.security.jwt.JwtProvider;
import com.example.customersms.config.security.principal.UserDetailsCus;
import com.example.customersms.dto.request.LoginRequest;
import com.example.customersms.dto.request.RegisterRequest;
import com.example.customersms.dto.response.AuthResponse;
import com.example.customersms.dto.response.UserResponse;
import com.example.customersms.entity.Roles;
import com.example.customersms.entity.User;
import com.example.customersms.mapper.UserMapper;
import com.example.customersms.repository.RoleRepository;
import com.example.customersms.repository.UserRepository;
import com.example.customersms.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Đăng nhập với username: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetailsCus userDetails = (UserDetailsCus) authentication.getPrincipal();

        // Sửa lỗi: truyền authentication thay vì userDetails
        String jwt = jwtProvider.generateToken(authentication);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("Đăng nhập thành công cho user: {}", userDetails.getUsername());

        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFullName(),
                roles
        );
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        log.info("Đăng ký tài khoản mới với username: {}", registerRequest.getUsername());

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã được sử dụng!");
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setStatus(User.UserStatus.ACTIVE);

        // Gán role CUSTOMER mặc định
        Set<Roles> roles = new HashSet<>();
        Roles customerRole = roleRepository.findByName(Roles.RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role CUSTOMER!"));
        roles.add(customerRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("Đăng ký thành công cho user: {}", savedUser.getUsername());

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void initDefaultRoles() {
        // Tạo các role mặc định nếu chưa tồn tại
        for (Roles.RoleName roleName : Roles.RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Roles role = new Roles();
                role.setName(roleName);
                role.setDescription(getDescriptionForRole(roleName));
                roleRepository.save(role);
                log.info("Đã tạo role: {}", roleName);
            }
        }
    }

    private String getDescriptionForRole(Roles.RoleName roleName) {
        return switch (roleName) {
            case ROLE_ADMIN -> "Quản trị viên hệ thống";
            case ROLE_STAFF -> "Nhân viên";
            case ROLE_CUSTOMER -> "Khách hàng";
        };
    }
}