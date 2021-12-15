package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class PawUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService us;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<User> user = us.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("No user by the name " + username);
        }
        List<SimpleGrantedAuthority> aut = new ArrayList<>();
        aut.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(user.get().isAdmin()){
            aut.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if(user.get().isEnable()){
            aut.add(new SimpleGrantedAuthority("ROLE_USER_ENABLED"));
        }
        if(user.get().isEnable() && user.get().isVerify()){
            aut.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        }
        final Collection<? extends GrantedAuthority> authorities = aut;
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), authorities);
    }

    public String generateToken(String username){
        final UserDetails userDetails = loadUserByUsername(username);
        return jwtTokenUtil.generateToken(userDetails);
    }
}
