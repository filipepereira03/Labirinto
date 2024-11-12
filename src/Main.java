import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            LabirintoHorror labirinto = new LabirintoHorror("caso6_7.txt");
            labirinto.identificarSeres();
            labirinto.contarRegioes();
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
