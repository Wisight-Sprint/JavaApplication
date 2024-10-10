package src.main.java;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        Boolean continuar = true;
        Integer tentativas = 3;
        do {
            String senha = "wisight123";
            String resposta_continuar;

            System.out.println("Para iniciar o sistema, informe a senha:");
            String leitura_senha = leitor.nextLine();

            if (leitura_senha.equals(senha) && tentativas > 1) {
                DateTimeFormatter formatacaoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                Integer milissegundos = 0;
                Integer segundos = 0;


                String[] logs = {
                        "[INFO] Inicializando aplicação: Carregando configurações...",
                        "[INFO] Conectando ao MySQL Server em localhost:8080...",
                        "[ERROR] Falha ao conectar no MySQL Server. Tentando novamente...",
                        "[INFO] MySQL Server conectado.",
                        "[INFO] Verificando integridade dos dados...",
                        "[WARN] Informação inconscistente detectado na tabela 'Usuario'. Processando com alerta.",
                        "[INFO] Aplicação iniciada com alerta."
                };

                for (String log : logs) {
                    milissegundos = ThreadLocalRandom.current().nextInt(1, 5);
                    segundos = milissegundos * 1000;

                    LocalDateTime now = LocalDateTime.now();
                    String dataAtual = now.format(formatacaoData);

                    System.out.println(dataAtual + " - " + log);

                    try {
                        Thread.sleep(segundos);
                    } catch (InterruptedException erro) {
                        erro.printStackTrace();
                    }
                }
                System.out.println("Deseja finalizar o programa? (s/n)");
                resposta_continuar = leitor.nextLine();
                if (resposta_continuar.equals("s")) {
                    System.out.println("Programa finalizado...");
                    continuar = false;
                }
            } else if (tentativas > 1) {
                tentativas--;
                System.out.println("Falha na autenticação. Tentar novamente? (s/n)");
                resposta_continuar = leitor.nextLine();
                if (resposta_continuar.equals("n")) {
                    System.out.println("Programa finalizado...");
                    continuar = false;
                }
            } else {
                System.out.println("Falha na autenticação. Número de tentativas excedidas. O programa será finalizado...");
                continuar = false;
            }
        } while (continuar);
    }
}
