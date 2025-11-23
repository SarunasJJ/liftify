package org.ecom.liftify.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.transaction.annotation.Transactional;
import org.ecom.liftify.entity.User;
import org.ecom.liftify.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService extends OidcUserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oauthUser = super.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setRole(User.Role.CUSTOMER);

            user = userRepository.save(newUser);
            userRepository.flush();
        }

        return new DefaultOidcUser(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oauthUser.getIdToken(),
                oauthUser.getUserInfo()
        );
    }
}
