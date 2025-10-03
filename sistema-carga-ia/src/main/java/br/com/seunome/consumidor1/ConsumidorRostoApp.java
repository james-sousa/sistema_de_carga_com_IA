package br.com.seunome.consumidor1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class ConsumidorRostoApp {
    private static final String NOME_EXCHANGE = "imagens_exchange";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(NOME_EXCHANGE, "topic");
        String nomeFila = channel.queueDeclare().getQueue();
        channel.queueBind(nomeFila, NOME_EXCHANGE, "face.*");

        System.out.println(" [*] Consumidor de ROSTOS esperando por imagens. Para sair, pressione CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String routingKey = delivery.getEnvelope().getRoutingKey();
            System.out.println(" [x] Recebido '" + routingKey + "'");
            try {
                System.out.println(" [ia-rosto] Analisando imagem...");
                Thread.sleep(2000);

                // ATUALIZADO: Adicionada a lógica para "raiva"
                if (routingKey.equalsIgnoreCase("face.happy")) {
                    System.out.println(" [ia-rosto] Análise concluída: Pessoa parece FELIZ.");
                } else if (routingKey.equalsIgnoreCase("face.sad")) {
                    System.out.println(" [ia-rosto] Análise concluída: Pessoa parece TRISTE.");
                } else if (routingKey.equalsIgnoreCase("face.angry")) {
                    System.out.println(" [ia-rosto] Análise concluída: Pessoa parece com RAIVA.");
                } else {
                    System.out.println(" [ia-rosto] Análise concluída: Emoção não identificada.");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        channel.basicConsume(nomeFila, true, deliverCallback, consumerTag -> { });
    }
}