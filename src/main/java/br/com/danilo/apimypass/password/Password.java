package br.com.danilo.apimypass.password;

import jakarta.persistence.*;
import lombok.Data;

@Table
@Entity(name = "passwords")
@Data
public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String url;
    String username;
    String password;
}
