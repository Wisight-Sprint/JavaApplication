package com.project.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String environment = "prod"; // Alterar para "dev" quando necessário
    private static final Properties properties = new Properties();

    static {
        if (environment.equals("prod")) {
            String[] variaveisAmbiente = {
                    "DB.HOST", "DB.PORT", "DB.NAME", "DB.URL", "DB.DRIVER", "DB.USER", "DB.PASSWORD",
                    "AWS.ACCESS.KEY.ID", "AWS.SECRET.ACCESS.KEY", "AWS.SESSION.TOKEN"
            };

            for (String chave : variaveisAmbiente) {
                String valorVariavel = System.getenv(chave);
                if (valorVariavel != null) {
                    properties.setProperty(chave, valorVariavel);
                    System.out.println("Variável de ambiente adicionada: " + chave + " = " + valorVariavel);
                } else {
                    System.err.println("Variável de ambiente '" + chave + "' não encontrada.");
                }
            }

        } else {
            String envFile = "config.dev.properties";
            try (InputStream input = Config.class.getClassLoader().getResourceAsStream(envFile)) {
                if (input != null) {
                    properties.load(input);
                    System.out.println("Configurações de desenvolvimento carregadas.");
                } else {
                    System.err.println("Arquivo config.dev.properties não encontrado: " + envFile);
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar o config.dev.properties: " + e.getMessage());
            }
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String getEnvironment() {
        return environment;
    }
}
