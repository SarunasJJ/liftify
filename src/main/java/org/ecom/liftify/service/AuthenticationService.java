package org.ecom.liftify.service;

import org.ecom.liftify.entity.User;
import org.ecom.liftify.exception.UnauthorizedException;
import org.ecom.liftify.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(){
        String email = getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Email does not exist"));
    }

    public Long getCurrentUserId(){
        return getCurrentUser().getId();
    }

    public String getCurrentUserFullName(){
        return getCurrentUser().getFullName();
    }

    public User.Role getCurrentUserRole(){
        return getCurrentUser().getRole();
    }

    public boolean hasRole(User.Role role){
        try {
            User user = getCurrentUser();
            return user.getRole().equals(role);
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    public boolean isAdmin(){
        return hasRole(User.Role.ADMIN);
    }

    public boolean isCustomer(){
        return hasRole(User.Role.CUSTOMER);
    }


    public String getCurrentUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new UnauthorizedException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof OidcUser oidcUser){
            String email = oidcUser.getAttribute("email");
            if(email == null || email.isEmpty()){
                throw new UnauthorizedException("Invalid email");
            }

            return email;
        }

        if("anonymousUser".equals(principal)){
            throw new UnauthorizedException("Unauthorized");
        }
        throw new UnauthorizedException("Unauthorized");
    }

    public boolean isAuthenticated(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                throw new UnauthorizedException("Unauthorized");
            }

            Object principal = authentication.getPrincipal();
            return principal instanceof OidcUser && !("anonymousUser".equals(principal));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCurrentUser(Long userId){
        try {
            return getCurrentUser().getId().equals(userId);
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    public boolean canAccess(Long userId){
        return isCurrentUser(userId) && isAdmin();
    }

    public OidcUser getOidcUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return oidcUser;
        }

        throw new UnauthorizedException("Invalid authentication type");
    }

    public <T> T getOAuth2Attribute(String attributeName) {
        try {
            OidcUser oidcUser = getOidcUser();
            return oidcUser.getAttribute(attributeName);
        } catch (UnauthorizedException e) {
            return null;
        }
    }
}
