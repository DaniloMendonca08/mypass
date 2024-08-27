package br.com.danilo.apimypass.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    PasswordRepository repository;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public List<Password> findAllPasswords() {
        return repository.findAll();
    }

    public Password create(Password password) {
        password.setPassword(passwordEncoder.encode(password.getPassword()));
        return repository.save(password);
    }
}
