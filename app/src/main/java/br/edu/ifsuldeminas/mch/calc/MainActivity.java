package br.edu.ifsuldeminas.mch.calc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ifsuldeminas.mch.calc";

    // Botões numéricos
    private Button buttonZero, buttonUm, buttonDois, buttonTres, buttonQuatro;
    private Button buttonCinco, buttonSeis, buttonSete, buttonOito, buttonNove;

    // Botões de operação
    private Button buttonSoma, buttonSubtracao, buttonMultiplicacao, buttonDivisao, buttonPorcento, buttonVirgula;

    // Botões de controle
    private Button buttonIgual, buttonReset, buttonDelete;

    // TextViews de exibição
    private TextView textViewResultado;
    private TextView textViewUltimaExpressao;

    // Variável para armazenar a expressão atual
    private String expressaoAtual = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        Log.d(TAG, "MainActivity onCreate iniciado");

        // Inicializar TextViews
        textViewResultado = findViewById(R.id.textViewResultadoID);
        textViewUltimaExpressao = findViewById(R.id.textViewUltimaExpressaoID);

        // Inicializar botões numéricos
        buttonZero = findViewById(R.id.buttonZeroID);
        buttonUm = findViewById(R.id.buttonUmID);
        buttonDois = findViewById(R.id.buttonDoisID);
        buttonTres = findViewById(R.id.buttonTresID);
        buttonQuatro = findViewById(R.id.buttonQuatroID);
        buttonCinco = findViewById(R.id.buttonCincoID);
        buttonSeis = findViewById(R.id.buttonSeisID);
        buttonSete = findViewById(R.id.buttonSeteID);
        buttonOito = findViewById(R.id.buttonOitoID);
        buttonNove = findViewById(R.id.buttonNoveID);

        // Inicializar botões de operação
        buttonSoma = findViewById(R.id.buttonSomaID);
        buttonSubtracao = findViewById(R.id.buttonSubtracaoID);
        buttonMultiplicacao = findViewById(R.id.buttonMultiplicacaoID);
        buttonDivisao = findViewById(R.id.buttonDivisaoID);
        buttonPorcento = findViewById(R.id.buttonPorcentoID);
        buttonVirgula = findViewById(R.id.buttonVirgulaID);

        // Inicializar botões de controle
        buttonIgual = findViewById(R.id.buttonIgualID);
        buttonReset = findViewById(R.id.buttonResetID);
        buttonDelete = findViewById(R.id.buttonDeleteID);

        // Definir listeners dos botões numéricos
        setNumericButtonListener(buttonZero, "0");
        setNumericButtonListener(buttonUm, "1");
        setNumericButtonListener(buttonDois, "2");
        setNumericButtonListener(buttonTres, "3");
        setNumericButtonListener(buttonQuatro, "4");
        setNumericButtonListener(buttonCinco, "5");
        setNumericButtonListener(buttonSeis, "6");
        setNumericButtonListener(buttonSete, "7");
        setNumericButtonListener(buttonOito, "8");
        setNumericButtonListener(buttonNove, "9");

        // Definir listeners dos botões de operação
        setOperationButtonListener(buttonSoma, "+");
        setOperationButtonListener(buttonSubtracao, "-");
        setOperationButtonListener(buttonMultiplicacao, "*");
        setOperationButtonListener(buttonDivisao, "/");
        setOperationButtonListener(buttonPorcento, "%");

        // Listener do botão vírgula (.)
        buttonVirgula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarVirgula();
            }
        });

        // Listener do botão igual (=)
        buttonIgual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularResultado();
            }
        });

        // Listener do botão Reset (C)
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expressaoAtual = "";
                textViewResultado.setText("0");
                textViewUltimaExpressao.setText("");
                Log.d(TAG, "Calculadora resetada");
            }
        });

        // Listener do botão Delete (D)
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expressaoAtual.isEmpty()) {
                    expressaoAtual = expressaoAtual.substring(0, expressaoAtual.length() - 1);
                    atualizarDisplay();
                    Log.d(TAG, "Último caractere deletado. Expressão: " + expressaoAtual);
                }
            }
        });

        // Inicializar display
        atualizarDisplay();
    }

    private void setNumericButtonListener(Button button, final String numero) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expressaoAtual += numero;
                atualizarDisplay();
                Log.d(TAG, "Número adicionado: " + numero + ". Expressão: " + expressaoAtual);
            }
        });
    }

    private void setOperationButtonListener(Button button, final String operacao) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Evitar adicionar operação se a expressão estiver vazia ou terminar com operador
                if (!expressaoAtual.isEmpty() &&
                    !expressaoAtual.endsWith("+") &&
                    !expressaoAtual.endsWith("-") &&
                    !expressaoAtual.endsWith("*") &&
                    !expressaoAtual.endsWith("/") &&
                    !expressaoAtual.endsWith("%")) {
                    expressaoAtual += operacao;
                    atualizarDisplay();
                    Log.d(TAG, "Operação adicionada: " + operacao + ". Expressão: " + expressaoAtual);
                }
            }
        });
    }

    private void calcularResultado() {
        if (expressaoAtual.isEmpty()) {
            return;
        }

        // Usar thread separada para não bloquear a UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Processar a porcentagem ANTES de calcular
                    String expressaoProcessada = processarPorcentagem(expressaoAtual);
                    
                    Log.d(TAG, "Expressão original: " + expressaoAtual);
                    Log.d(TAG, "Expressão processada: " + expressaoProcessada);
                    
                    Expression avaliadorExpressao = new ExpressionBuilder(expressaoProcessada).build();
                    final double resultado = avaliadorExpressao.evaluate();
                    final String expressaoTemp = expressaoAtual;

                    // Atualizar UI na thread principal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Exibir na tela anterior a expressão formatada
                            String expressaoFormatada = formatarExpressaoParaExibicao(expressaoTemp);
                            textViewUltimaExpressao.setText(expressaoFormatada);

                            // Formatar resultado (remover casas decimais se for inteiro)
                            if (resultado == (long) resultado) {
                                String resultadoInteiro = String.format("%d", (long) resultado);
                                String resultadoFormatado = formatarNumero(resultadoInteiro);
                                textViewResultado.setText(resultadoFormatado);
                                // Armazenar como inteiro (sem ponto decimal)
                                expressaoAtual = resultadoInteiro;
                            } else {
                                // Formatar com até 8 casas decimais, removendo zeros à direita
                                String resultadoFormatado = String.format(java.util.Locale.getDefault(), "%.8f", resultado);
                                // Remove zeros à direita
                                resultadoFormatado = resultadoFormatado.replaceAll("0+$", "");
                                // Se terminar com ponto, remove
                                resultadoFormatado = resultadoFormatado.replaceAll("\\.$", "");
                                // Formata com separadores
                                String resultadoExibicao = formatarNumero(resultadoFormatado);
                                textViewResultado.setText(resultadoExibicao);
                                // Armazenar com decimais
                                expressaoAtual = resultadoFormatado;
                            }

                            Log.d(TAG, "Cálculo realizado: " + expressaoAtual);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao calcular expressão", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewResultado.setText("Erro");
                            expressaoAtual = "";
                        }
                    });
                }
            }
        }).start();
    }

    private String processarPorcentagem(String expressao) {
        // Se não contém %, retorna a expressão original
        if (!expressao.contains("%")) {
            return expressao;
        }

        // Encontrar o operador que vem antes do %
        int indicePorcentagem = expressao.indexOf("%");
        if (indicePorcentagem == -1) {
            return expressao;
        }

        // Encontrar qual é o último operador antes do %
        String operador = null;
        int indiceOperador = -1;

        for (int i = indicePorcentagem - 1; i >= 0; i--) {
            char c = expressao.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                operador = String.valueOf(c);
                indiceOperador = i;
                break;
            }
        }

        // Se não encontrou operador, converter apenas o número para decimal
        if (operador == null || indiceOperador == -1) {
            String numero = expressao.substring(0, indicePorcentagem).trim();
            try {
                double valor = Double.parseDouble(numero);
                return String.valueOf(valor / 100);
            } catch (Exception e) {
                return expressao;
            }
        }

        // Extrair os números antes e depois do operador
        String numeroAnteriorStr = expressao.substring(0, indiceOperador).trim();
        String percentualStr = expressao.substring(indiceOperador + 1, indicePorcentagem).trim();

        try {
            double numeroAnterior = Double.parseDouble(numeroAnteriorStr);
            double percentual = Double.parseDouble(percentualStr);

            double novoValor;

            // Lógica contextual baseada no operador
            if (operador.equals("+") || operador.equals("-")) {
                // Para soma e subtração: calcular porcentagem em relação ao primeiro número
                // Exemplo: 200 + 50% -> 200 + (50% de 200) = 200 + 100
                novoValor = (percentual * numeroAnterior) / 100;
            } else {
                // Para multiplicação e divisão: converter para decimal
                // Exemplo: 100 * 50% -> 100 * 0.5
                novoValor = percentual / 100;
            }

            // Montar a nova expressão
            return numeroAnteriorStr + operador + novoValor;

        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar porcentagem", e);
            return expressao;
        }
    }

    private void atualizarDisplay() {
        if (expressaoAtual.isEmpty()) {
            textViewResultado.setText("0");
        } else {
            // Exibir a expressão com formatação correta
            String expressaoFormatada = formatarExpressaoParaExibicao(expressaoAtual);
            textViewResultado.setText(expressaoFormatada);
        }
    }

    private String formatarExpressaoParaExibicao(String expressao) {
        // Esta função formata a expressão para exibição no display
        // Converte pontos em vírgulas para decimais e adiciona separadores de milhares
        
        StringBuilder resultado = new StringBuilder();
        int i = 0;
        
        while (i < expressao.length()) {
            char c = expressao.charAt(i);
            
            // Se for um operador, adiciona diretamente
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                resultado.append(c);
                i++;
            } else {
                // Extrai o número completo (incluindo o ponto)
                StringBuilder numero = new StringBuilder();
                while (i < expressao.length() && Character.isDigit(expressao.charAt(i)) || 
                       (i < expressao.length() && expressao.charAt(i) == '.')) {
                    numero.append(expressao.charAt(i));
                    i++;
                }
                
                // Formata o número
                String numeroFormatado = formatarNumero(numero.toString());
                resultado.append(numeroFormatado);
            }
        }
        
        return resultado.toString();
    }

    private String formatarNumero(String numero) {
        // Formata um número: "1000.5" vira "1.000,5"
        // "1234567" vira "1.234.567"
        // "1000." vira "1.000," (com vírgula no final mesmo sem decimal)
        
        if (numero.isEmpty()) {
            return numero;
        }
        
        // Verificar se termina com ponto (indica que o usuário clicou na vírgula)
        boolean temVirgulaAoFinal = numero.endsWith(".");
        
        // Separar a parte inteira da parte decimal
        String[] partes = numero.split("\\.");
        String parteInteira = partes[0];
        String parteDecimal = partes.length > 1 ? partes[1] : "";
        
        // Formatar a parte inteira com separadores de milhares
        StringBuilder inteitoFormatada = new StringBuilder();
        int contador = 0;
        
        for (int i = parteInteira.length() - 1; i >= 0; i--) {
            if (contador == 3) {
                inteitoFormatada.insert(0, '.');
                contador = 0;
            }
            inteitoFormatada.insert(0, parteInteira.charAt(i));
            contador++;
        }
        
        // Montar o resultado final
        String resultado = inteitoFormatada.toString();
        if (!parteDecimal.isEmpty() || temVirgulaAoFinal) {
            resultado += "," + parteDecimal;
        }
        
        return resultado;
    }


    private int encontrarUltimoOperador(String expressao) {
        int indiceSoma = expressao.lastIndexOf("+");
        int indiceSubtracao = expressao.lastIndexOf("-");
        int indiceMultiplicacao = expressao.lastIndexOf("*");
        int indiceDivisao = expressao.lastIndexOf("/");

        // Encontrar o maior índice entre os operadores
        return Math.max(Math.max(indiceSoma, indiceSubtracao), Math.max(indiceMultiplicacao, indiceDivisao));
    }

    private void adicionarVirgula() {
        // Adiciona a vírgula (ou ponto) na expressão atual, tratando casos especiais
        if (expressaoAtual.isEmpty()) {
            // Se a expressão está vazia, adiciona "0,"
            expressaoAtual = "0.";
        } else if (expressaoAtual.equals("Erro")) {
            // Se a expressão atual é "Erro", reseta a calculadora
            expressaoAtual = "0.";
        } else if (expressaoAtual.endsWith("+") || expressaoAtual.endsWith("-") ||
                   expressaoAtual.endsWith("*") || expressaoAtual.endsWith("/") ||
                   expressaoAtual.endsWith("%")) {
            // Se termina com operador, adiciona "0," (novo número)
            expressaoAtual += "0.";
        } else {
            // Verifica se já existe uma vírgula no número atual
            int indiceUltimoOperador = encontrarUltimoOperador(expressaoAtual);
            String parteDecimal;

            if (indiceUltimoOperador == -1) {
                // Não há operador, então toda a expressão é o número
                parteDecimal = expressaoAtual;
            } else {
                // Pega apenas a parte após o último operador
                parteDecimal = expressaoAtual.substring(indiceUltimoOperador + 1);
            }

            // Se não contém ponto/vírgula, adiciona
            if (!parteDecimal.contains(".")) {
                expressaoAtual += ".";
            } else {
                Log.d(TAG, "Vírgula já existe neste número. Operação bloqueada.");
                return;
            }
        }

        atualizarDisplay();
        Log.d(TAG, "Vírgula adicionada. Expressão: " + expressaoAtual);
    }
}
