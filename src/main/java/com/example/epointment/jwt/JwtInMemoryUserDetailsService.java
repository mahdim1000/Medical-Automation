package com.example.epointment.jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.epointment.common.Users;
import com.example.epointment.config.DataConfiguration;
import com.example.epointment.model.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtInMemoryUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    static List<JwtUserDetails> inMemoryUserList = new ArrayList<>();

    static {
//        inMemoryUserList.add(new JwtUserDetails(1L, "in28minutes",
//                "$2a$10$3zHzb.Npv1hfZbLEU5qsdOju/tk2je6W6PnNnY.c1ujWPcZh4PL6e", "ROLE_USER_2"));
//        inMemoryUserList.add(new JwtUserDetails(2L, "ranga",
//                "$2a$10$IetbreuU5KihCkDB6/r1DOJO0VyU9lSiBcrMDT.biU7FOt2oqZDPm", "ROLE_USER_2"));
//        inMemoryUserList.add(new JwtUserDetails(3L, "a",
//                "$2a$10$ioUrLI1vNgRcQ93LiXuBguEssxQdcKKNQg3LoAzH1MWch7deZV29u", "ROLE_USER_2"));

        //$2a$10$IetbreuU5KihCkDB6/r1DOJO0VyU9lSiBcrMDT.biU7FOt2oqZDPm
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<JwtUserDetails> findFirst = inMemoryUserList.stream()
//                .filter(user -> user.getUsername().equals(username)).findFirst();

        Users u = userDao.findByMelliCode(username);
        Users user  = new Users(u.getId(), u.getMelliCode(), u.getName(), u.getPassword(), u.getPhone(), u.getRole());
//        user.setPassword(DataConfiguration.getSHA512(user.getPassword()));
        Optional<JwtUserDetails> findFirst = Optional.of(new JwtUserDetails(user.getId(), user.getMelliCode(), user.getPassword(), user.getRole()));
//
        if (!findFirst.isPresent()) {
            throw new UsernameNotFoundException(String.format("USER_NOT_FOUND '%s'.", username));
        }
//
        return findFirst.get();
    }

}