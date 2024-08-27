package br.com.danilo.apimypass.auth;

import br.com.danilo.apimypass.user.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public AuthorizationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var header = request.getHeader("Authorization");
        if (header == null ) {
            //passando para o próximo filtro caso o usuário não passe o header, ex: requisição POST para criar conta
            filterChain.doFilter(request, response);
            return;
        }

        //caso o header não comece com Bearer
        if(!header.startsWith("Bearer ")) {

            //adicionando o status code 401 à resposta (unauthorized)
            response.setStatus(401);

            //adicionando header indicando que retornará um json
            response.addHeader("Content-Type", "application/json");

            //definindo a mensagem do json
            response.getWriter().write("""
                    {
                        "message": "Token must start with Bearer"
                    }
                    """);
            return;

        }

            try {
                //pegando o token do header e removendo a parte do Bearer
                //para pegarmos apenas o token em si
                var token = header.replace("Bearer ", "");

                User user = tokenService.getUserFromToken(token);

                //autorizando o uusário
                var auth = new UsernamePasswordAuthenticationToken(
                        user.getUsername(), // O nome do usuário
                        user.getPassword(), // A senha dele
                        List.of(new SimpleGrantedAuthority("USER")) // Autoridades vazias (ajuste conforme necessário)
                );

                //definindo a autenticação no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);

            //caso ocorra algum erro na autenticação do token
            } catch (Exception e) {
                response.setStatus(403);
                response.addHeader("Content-Type", "application/json");
                response.getWriter().write("""
                        {
                            "message": "%se.getMessage()"
                        }
                        """.formatted(e.getMessage()));
                return;
            }
        }
    }

