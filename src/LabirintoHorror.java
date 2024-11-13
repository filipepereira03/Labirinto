import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LabirintoHorror {
    private int m, n;
    private String[][] labirinto;
    private boolean[][] visitado;
    private final int[][] DIRECOES = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
    private Map<Character, Integer> contagemSeres;
    private static final Map<Character, String> SERES_NOMES = new HashMap<>();
    static {
        SERES_NOMES.put('A', "Anão");
        SERES_NOMES.put('B', "Bruxa");
        SERES_NOMES.put('C', "Cavaleiro");
        SERES_NOMES.put('D', "Duende");
        SERES_NOMES.put('E', "Elfo");
        SERES_NOMES.put('F', "Feijão");
    }


    // Construtor da classe LabirintoHorror
    public LabirintoHorror(String caminhoArquivo) throws IOException {
        lerArquivo(caminhoArquivo);
        this.visitado = new boolean[m][n];
        this.contagemSeres = new HashMap<>();
    }


    // Método para ler o arquivo de entrada
    // O arquivo de entrada deve estar no formato: .txt
    // O arquivo de entrada deve conter as dimensões do labirinto (m x n) na primeira linha
    private void lerArquivo(String caminhoArquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String[] dimensoes = reader.readLine().split(" ");
            this.m = Integer.parseInt(dimensoes[0]);
            this.n = Integer.parseInt(dimensoes[1]);
            this.labirinto = new String[m][n];

            for (int i = 0; i < m; i++) {
                String[] linha = reader.readLine().split(" ");
                if (n >= 0) System.arraycopy(linha, 0, labirinto[i], 0, n);
            }
        }
    }

    // Método para identificar os seres no labirinto
    // O método percorre o labirinto e identifica as posições dos seres
    // Os seres são identificados pelas letras maiúsculas de A a F
    public void identificarSeres() {
        // System.out.println("Conteúdo de cada célula e posições das letras maiúsculas
        // (seres) no labirinto:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String cellValue = labirinto[i][j];
                // System.out.println("Célula [" + i + "][" + j + "] contém: " + cellValue);
                for (int k = 0; k < cellValue.length(); k++) {
                    char c = cellValue.charAt(k);
                    if (c >= 'A' && c <= 'F') {
                        // System.out.println("Ser " + SERES_NOMES.get(c) + " (" + c + ") encontrado na
                        // posição [" + i + "][" + j + "]");
                    }
                }
            }
        }
    }

    // Método para contar as regiões isoladas no labirinto
    // O método percorre o labirinto e conta as regiões isoladas
    // Uma região isolada é uma região onde não há conexão entre as células
    // O método também identifica o ser mais comum em cada região e no labirinto
    public void contarRegioes() {
        int regioes = 0;

        contagemSeres = new HashMap<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (!visitado[i][j]) {
                    Map<Character, Integer> seresRegiao = new HashMap<>();
                    explorarRegiao(i, j, seresRegiao);
                    regioes++;

                    Character serMaisComumRegiao = obterSerMaisComumRegiao(seresRegiao);
                    if (serMaisComumRegiao != null) {
                        System.out.println("Região " + regioes + ": Ser mais comum: "
                                + SERES_NOMES.get(serMaisComumRegiao) + " (" + serMaisComumRegiao + ")");
                    } else {
                        System.out.println("Região " + regioes + ": Nenhum ser encontrado");
                    }

                    atualizarSerMaisComum(seresRegiao);
                }
            }
        }

        System.out.println("Número de regiões isoladas: " + regioes);

        Character serMaisComumGeral = obterSerMaisComum();
        if (serMaisComumGeral != null) {
            System.out.println("Ser mais comum em todo o labirinto: " + SERES_NOMES.get(serMaisComumGeral) + " ("
                    + serMaisComumGeral + ")");
        }
    }

    // Método para explorar uma região no labirinto
    // O método utiliza uma pilha para percorrer as células da região
    // O método utiliza um mapa para contar a quantidade de seres na região

    private void explorarRegiao(int Ix, int Iy, Map<Character, Integer> seresRegiao) {
        Stack<int[]> pilha = new Stack<>();
        pilha.push(new int[] { Ix, Iy});
        visitado[Ix][Iy] = true;

        while (!pilha.isEmpty()) {
            int[] pos = pilha.pop();
            int x = pos[0];
            int y = pos[1];

            String cellValue = labirinto[x][y];
            for (int k = 0; k < cellValue.length(); k++) {
                char c = cellValue.charAt(k);
                if (isSer(c)) {
                    seresRegiao.put(c, seresRegiao.getOrDefault(c, 0) + 1);
                }
            }

            for (int[] dir : DIRECOES) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (dentroDosLimites(nx, ny) && !visitado[nx][ny] && temConexao(x, y, nx, ny)) {
                    visitado[nx][ny] = true;
                    pilha.push(new int[] { nx, ny });
                }
            }
        }
    }


    // Métodos auxiliares
    private boolean isSer(char c) {
        return c >= 'A' && c <= 'F';
    }

    private boolean dentroDosLimites(int x, int y) {
        return x >= 0 && x < m && y >= 0 && y < n;
    }

    private int getParede(String cellValue) {
        for (int i = 0; i < cellValue.length(); i++) {
            char c = cellValue.charAt(i);
            if (Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                return Integer.parseInt(String.valueOf(c), 16);
            }
        }
        return -1;
    }


    // O método verifica se há uma conexão entre as células
    // O método verifica se há uma parede entre as células
    // O método retorna true se houver conexão entre as células e false caso contrário
    private boolean temConexao(int x, int y, int nx, int ny) {
        int valorAtual = getParede(labirinto[x][y]);
        int valorVizinho = getParede(labirinto[nx][ny]);

        if (valorAtual == -1 || valorVizinho == -1) {
            return false;
        }

        boolean paredeSuperiorAtual = (valorAtual & 8) != 0;
        boolean paredeDireitaAtual = (valorAtual & 4) != 0;
        boolean paredeInferiorAtual = (valorAtual & 2) != 0;
        boolean paredeEsquerdaAtual = (valorAtual & 1) != 0;

        boolean paredeSuperiorVizinho = (valorVizinho & 8) != 0;
        boolean paredeDireitaVizinho = (valorVizinho & 4) != 0;
        boolean paredeInferiorVizinho = (valorVizinho & 2) != 0;
        boolean paredeEsquerdaVizinho = (valorVizinho & 1) != 0;

        if (nx == x - 1)
            return !paredeSuperiorAtual && !paredeInferiorVizinho;
        if (nx == x + 1)
            return !paredeInferiorAtual && !paredeSuperiorVizinho;
        if (ny == y + 1)
            return !paredeDireitaAtual && !paredeEsquerdaVizinho;
        if (ny == y - 1)
            return !paredeEsquerdaAtual && !paredeDireitaVizinho;

        return false;
    }

    // Métodos para atualizar e obter o ser mais comum
    // O método atualizarSerMaisComum atualiza o mapa de contagem de seres
    private void atualizarSerMaisComum(Map<Character, Integer> seresRegiao) {
        for (Map.Entry<Character, Integer> entry : seresRegiao.entrySet()) {
            contagemSeres.put(entry.getKey(), contagemSeres.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    // O método obterSerMaisComum retorna o ser mais comum no mapa de contagem de seres
    private Character obterSerMaisComum() {
        if (contagemSeres.isEmpty()) {
            return null;
        }
        return Collections.max(contagemSeres.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    // O método obterSerMaisComumRegiao retorna o ser mais comum no mapa de contagem de seres de uma região
    private Character obterSerMaisComumRegiao(Map<Character, Integer> seresRegiao) {
        if (seresRegiao.isEmpty()) {
            return null;
        }
        return Collections.max(seresRegiao.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
