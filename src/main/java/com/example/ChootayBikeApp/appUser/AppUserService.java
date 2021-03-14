package com.example.ChootayBikeApp.appUser;

import com.example.ChootayBikeApp.registration.token.ConfirmationToken;
import com.example.ChootayBikeApp.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND = "User with Email %s not found";
    private final AppUserRepo aUserRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return aUserRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    public String signUpUser(AppUser appUser)
    {
        boolean userExists = aUserRepo.findByEmail(appUser.getEmail()).isPresent();
        if(userExists)
        {
            throw new IllegalStateException("Email Already Taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        aUserRepo.save(appUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);
   //     appUser.isAccountNonLocked();
        return token;
    }
    public int enableAppUser(String email) {
        return aUserRepo.enableAppUser(email);
    }

    //save
    public void saveUser(AppUser appUser ) { aUserRepo.save(appUser);}

    public AppUser getUser(String username){ return aUserRepo.findUserByFirstName(username); }

    public AppUser getUser(Long id){ return aUserRepo.findUserByID(id); }

    public List<AppUser> getAllUsers(){ return aUserRepo.findAll(); }

    //public List<AppUser> getSearchedUser(String search){ return aUserRepo.findAllByUsernameContainingOrEmailContaining(search,search);}
}
