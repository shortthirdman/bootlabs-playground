package com.shortthirdman.bootlabs.security.service;

import com.shortthirdman.bootlabs.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service(value = "userDetailsJwtService")
public class UserDetailsJwtServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));

        // set encoded password = 123456789  (this is for dev mode)
        user.setPassword("$2a$10$NWNajrXEnFlv9nekF7G6xOhDabCMCYhYNrIkqgRVbHASfZpgb6EWS");
        detailsChecker.check(user);

        return user;
    }
}
