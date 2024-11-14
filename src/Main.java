import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            LabirintoHorror labirinto = new LabirintoHorror("caso40.txt");
            labirinto.identificarSeres();
            labirinto.contarRegioes();
        } catch (IOException e) {
            // verificação padrão
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
