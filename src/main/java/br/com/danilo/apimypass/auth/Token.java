package br.com.danilo.apimypass.auth;

public record Token(String token, String type, String username) {
}