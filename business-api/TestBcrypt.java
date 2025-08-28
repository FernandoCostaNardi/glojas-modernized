import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBcrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("Senha: " + password);
        System.out.println("Hash gerado: " + hash);
        System.out.println("Hash atual na migração: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
        
        // Verificar se o hash atual está correto
        boolean matches = encoder.matches(password, "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
        System.out.println("Hash atual está correto? " + matches);
        
        // Gerar um novo hash para usar
        String newHash = encoder.encode(password);
        System.out.println("Novo hash para usar: " + newHash);
        System.out.println("Novo hash está correto? " + encoder.matches(password, newHash));
    }
}
