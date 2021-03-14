package com.example.ChootayBikeApp.appUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAll();

    AppUser findUserByID(Long id);

    AppUser findUserByFirstName(String FirstName);

//    @Query("UPDATE AppUser a " +
//            "SET a.enabled = TRUE WHERE a.email = ?1")
//    int



    //AppUser findByUsername(String s);

    //List<AppUser> findAllByUsernameContainingOrEmailContaining(String username, String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);
}
