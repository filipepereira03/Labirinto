import java.io.*;
import java.util.*;

public class LabirintoHorror {
    private int m, n;
    private String[][] labirinto;
    private boolean[][] visitado;
    private static final int[][] DIRECOES = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
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

    public LabirintoHorror(String caminhoArquivo) throws IOException {
        lerArquivo(caminhoArquivo);
        this.visitado = new boolean[m][n];
        this.contagemSeres = new HashMap<>();
    }

    private void lerArquivo(String caminhoArquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String[] dimensoes = reader.readLine().split(" ");
            this.m = Integer.parseInt(dimensoes[0]);
            this.n = Integer.parseInt(dimensoes[1]);
            this.labirinto = new String[m][n];

            for (int i = 0; i < m; i++) {
                String[] linha = reader.readLine().split(" ");
                for (int j = 0; j < n; j++) {
                    labirinto[i][j] = linha[j];
                }
            }
        }
    }

    public void identificarSeres() {
        //System.out.println("Conteúdo de cada célula e posições das letras maiúsculas (seres) no labirinto:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String cellValue = labirinto[i][j];
                //System.out.println("Célula [" + i + "][" + j + "] contém: " + cellValue);
                for (int k = 0; k < cellValue.length(); k++) {
                    char c = cellValue.charAt(k);
                    if (c >= 'A' && c <= 'F') {
                        //System.out.println("Ser " + SERES_NOMES.get(c) + " (" + c + ") encontrado na posição [" + i + "][" + j + "]");
                    }
                }
            }
        }
    }

    public void contarRegioes() {
        int regioes = 0;

        contagemSeres = new HashMap<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (!visitado[i][j]) {
                    Map<Character, Integer> seresRegiao = new HashMap<>();
                    explorarRegiaoIterativo(i, j, seresRegiao);
                    regioes++;


                    Character serMaisComumRegiao = obterSerMaisComumRegiao(seresRegiao);
                    if (serMaisComumRegiao != null) {
                        System.out.println("Região " + regioes + ": Ser mais comum: " + SERES_NOMES.get(serMaisComumRegiao) + " (" + serMaisComumRegiao + ")");
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
            System.out.println("Ser mais comum em todo o labirinto: " + SERES_NOMES.get(serMaisComumGeral) + " (" + serMaisComumGeral + ")");
        } else {
            System.out.println("Nenhum ser encontrado em todo o labirinto");
        }
    }


    private void explorarRegiaoIterativo(int startX, int startY, Map<Character, Integer> seresRegiao) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});
        visitado[startX][startY] = true;

        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
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
                    stack.push(new int[]{nx, ny});
                }
            }
        }
    }

    private boolean isSer(char c) {
        return c >= 'A' && c <= 'F';
    }

    private boolean dentroDosLimites(int x, int y) {
        return x >= 0 && x < m && y >= 0 && y < n;
    }

    private int getWallValue(String cellValue) {
        for (int i = 0; i < cellValue.length(); i++) {
            char c = cellValue.charAt(i);
            if (Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                return Integer.parseInt(String.valueOf(c), 16);
            }
        }
        return -1;
    }

    private boolean temConexao(int x, int y, int nx, int ny) {
        int valorAtual = getWallValue(labirinto[x][y]);
        int valorVizinho = getWallValue(labirinto[nx][ny]);

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

        if (nx == x - 1) return !paredeSuperiorAtual && !paredeInferiorVizinho;
        if (nx == x + 1) return !paredeInferiorAtual && !paredeSuperiorVizinho;
        if (ny == y + 1) return !paredeDireitaAtual && !paredeEsquerdaVizinho;
        if (ny == y - 1) return !paredeEsquerdaAtual && !paredeDireitaVizinho;

        return false;
    }

    private void atualizarSerMaisComum(Map<Character, Integer> seresRegiao) {
        for (Map.Entry<Character, Integer> entry : seresRegiao.entrySet()) {
            contagemSeres.put(entry.getKey(), contagemSeres.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    private Character obterSerMaisComum() {
        if (contagemSeres.isEmpty()) {
            return null;
        }
        return Collections.max(contagemSeres.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private Character obterSerMaisComumRegiao(Map<Character, Integer> seresRegiao) {
        if (seresRegiao.isEmpty()) {
            return null;
        }
        return Collections.max(seresRegiao.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

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
