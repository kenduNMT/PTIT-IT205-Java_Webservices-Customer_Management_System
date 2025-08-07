package com.example.customersms.config.security.principal;

import com.example.customersms.entity.User;
import com.example.customersms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceCus implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        // Kiểm tra user có bị soft delete không
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new UsernameNotFoundException("User account has been deleted");
        }

        return UserDetailsCus.build(user);
    }
}