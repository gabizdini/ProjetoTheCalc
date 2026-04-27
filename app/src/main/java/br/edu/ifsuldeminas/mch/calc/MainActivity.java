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
    private Button buttonSoma, buttonSubtracao, buttonMultiplicacao, buttonDivisao, buttonPorcento;

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
                            // Exibir na tela anterior
                            textViewUltimaExpressao.setText(expressaoTemp);

                            // Formatar resultado (remover casas decimais se for inteiro)
                            if (resultado == (long) resultado) {
                                textViewResultado.setText(String.format("%d", (long) resultado));
                            } else {
                                textViewResultado.setText(String.format(java.util.Locale.getDefault(), "%.2f", resultado));
                            }

                            // Atualizar expressão para o próximo cálculo
                            expressaoAtual = String.valueOf(resultado);

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
            textViewResultado.setText(expressaoAtual);
        }
    }


    private int encontrarUltimoOperador(String expressao) {
        int indiceSoma = expressao.lastIndexOf("+");
        int indiceSubtracao = expressao.lastIndexOf("-");
        int indiceMultiplicacao = expressao.lastIndexOf("*");
        int indiceDivisao = expressao.lastIndexOf("/");

        // Encontrar o maior índice entre os operadores
        return Math.max(Math.max(indiceSoma, indiceSubtracao), Math.max(indiceMultiplicacao, indiceDivisao));
    }
}