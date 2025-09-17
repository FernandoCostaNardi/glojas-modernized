// Teste simples para verificar a formatação do ID
public class TestStoreFormat {
    public static void main(String[] args) {
        // Simula um ID de loja
        Long storeId = 1L;
        
        // Formata o ID com 6 dígitos
        String formattedId = String.format("%06d", storeId);
        
        System.out.println("ID original: " + storeId);
        System.out.println("ID formatado: " + formattedId);
        
        // Testa com outros valores
        System.out.println("ID 123 formatado: " + String.format("%06d", 123L));
        System.out.println("ID 999999 formatado: " + String.format("%06d", 999999L));
    }
}
