package com.project.services;

import com.project.config.Config;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import java.io.IOException;

public class SlackMessageSender {
    public static void sendMessageToSlack() {

        String token = Config.get("TOKEN_SLACK");
        String channel = "#data-e-analytics";
        String message = "Base de Dados atualizada, novas informações na Dashboard";

        Slack slack = Slack.getInstance();

        try {
            ChatPostMessageResponse response = slack.methods(token).chatPostMessage(req -> req
                    .channel(channel)
                    .text(message));

            if (response.isOk()) {
                System.out.println("Mensagem Slack enviada com sucesso!");
            } else {
                System.out.println("Erro ao enviar mensagem: " + response.getError());
            }
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
            System.out.println("Erro ao comunicar com a API do Slack: " + e.getMessage());
        }
    }
}

