package br.com.danilo.apimypass.auth;

import br.com.danilo.apimypass.user.User;
import br.com.danilo.apimypass.user.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private final UserRepository userRepository;
    Algorithm algorithm = Algorithm.HMAC256("assinatura");

    public TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Token create(User user) {

        //variavel contendo o tempo que irá expirar e convertendo para o horário brasileiro
        var expiresAt = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.ofHours(-3));
        Algorithm algorithm = Algorithm.HMAC256("assinatura");
        String token = JWT.create()
                .withIssuer("mypass")
                .withSubject(user.getUsername())
                .withClaim("role", "admin")
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return new Token(token, "JWT", user.getUsername());
    }

    public User getUserFromToken(String token) {
        var username = JWT.require(algorithm)
                .withIssuer("mypass")
                //cria uma instãncia do JWTVerifier
                .build()
                //passa o token para o JWTVerifier
                .verify(token)
                .getSubject();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

}
