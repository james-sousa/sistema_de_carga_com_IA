package br.com.seunome.consumidor2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class ConsumidorTimeApp {
    private static final String NOME_EXCHANGE = "imagens_exchange";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(NOME_EXCHANGE, "topic");
        String nomeFila = channel.queueDeclare().getQueue();
        channel.queueBind(nomeFila, NOME_EXCHANGE, "team.*"); // Continua pegando tudo que começa com "team."

        System.out.println(" [*] Consumidor de TIMES esperando por imagens. Para sair, pressione CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String routingKey = delivery.getEnvelope().getRoutingKey();
            System.out.println(" [x] Recebido '" + routingKey + "'");
            try {
                System.out.println(" [ia-time] Classificando imagem...");
                Thread.sleep(3000); // Simula processamento lento

                // ATUALIZADO: Lógica para classificação binária
                if (routingKey.equalsIgnoreCase("team.brasao")) {
                    System.out.println(" [ia-time] Análise concluída: A imagem É um brasão de futebol.");
                } else if (routingKey.equalsIgnoreCase("team.nao_brasao")) {
                    System.out.println(" [ia-time] Análise concluída: A imagem NÃO É um brasão de futebol.");
                } else {
                    System.out.println(" [ia-time] Análise concluída: Classificação desconhecida.");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        channel.basicConsume(nomeFila, true, deliverCallback, consumerTag -> { });
    }
}