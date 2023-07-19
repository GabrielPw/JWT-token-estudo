package org.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main {
    private final static String secret = "Senha_Super-secreta123.-2";
    List<Usuario> usuarios = new ArrayList<>(List.of(
            new Usuario("Gabriel", "gabriel@gmail.com", "gabriel123"),
            new Usuario("Rafael", "rafael@gmail.com", "rafael123"),
            new Usuario("Isabela", "isabela@gmail.com", "isabela123")
    ));
    HashMap<String, String> tokens = new HashMap<>();

    public static void main(String[] args) {

        Main main = new Main();

        String email = "gabriel@gmail.com";
        String senha = "gabriel123";

        String token = main.doLogin(email, senha);
        isTokenValid(token);
        System.out.println("Token Gerado: " + token);

        int seconds = 0;
        int waitTime = 60; // Tempo de espera em segundos
        System.out.println("Aguardando " + waitTime + " segundos...");

        while (seconds < waitTime) {
            try {
                Thread.sleep(1000); // Pausa por 1 segundo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            seconds++;

            // exibe de 10 em 10 segundos.
            if(seconds % 10 == 0)
                System.out.println("Segundos decorridos: " + seconds);

        }

        main.isTokenValid(token);
    }

    public String doLogin(String email, String password){
        for (Usuario user : usuarios){
            // tenta fazer login.
            if(user.getEmail().equals(email) && user.getSenha().equals(password)){
                if (!tokens.containsKey(email)) { // Verifica se o token já foi gerado
                    String token = createToken(email);
                    tokens.put(email, token); // Armazena o token gerado
                    return token;
                } else {
                    System.out.println("Já existe um token associado a este email.");
                    String token = tokens.get(email);
                    return token;
                }
            }
        }
        return null;
    }

    public static String createToken(String email){
        try {
            Date now = new Date();
            Date expiresAt = new Date(now.getTime() + 1000 * 60); // 1 min

            String token = JWT.create()
                    .withIssuer("GabrielXavier")
                    .withClaim("email", email)
                    .withExpiresAt(expiresAt)
                    .sign(Algorithm.HMAC256(Main.secret));
            System.out.println("Token gerado.");
            return token;
        } catch (JWTCreationException exception){
            throw new RuntimeException("You need to enable Algorithm.HMAC256");
        }
    }

    public static boolean isTokenValid(String token){

        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC256(Main.secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify an specific claim validations
                    .withIssuer("GabrielXavier")
                    .withClaim("email", "gabriel@gmail.com")
                    //.acceptExpiresAt(new Date().getTime())
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(token);
            System.out.println("Token válido.");
            return true;
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            System.out.println("TOKEN INVÁLIDO");
            return false;
        }
    }
}