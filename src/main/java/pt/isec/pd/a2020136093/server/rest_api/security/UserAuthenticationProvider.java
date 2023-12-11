package pt.isec.pd.a2020136093.server.rest_api.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pt.isec.pd.a2020136093.server.model.jdbc.ManageDB;
import pt.isec.pd.a2020136093.server.rest_api.Application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component
public class UserAuthenticationProvider implements AuthenticationProvider
{
    ManageDB manageDB = Application.getManageDB();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (username.equals("admin") && password.equals("admin")) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));

            Application.getClientData().setEmail(username);
            Application.getClientData().setAdmin(true);

            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }
        else {
            try {
                if(manageDB.login(username, password)){
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("USER"));

                    Application.getClientData().setEmail(username);
                    Application.getClientData().setAdmin(false);

                    return new UsernamePasswordAuthenticationToken(username, password, authorities);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
