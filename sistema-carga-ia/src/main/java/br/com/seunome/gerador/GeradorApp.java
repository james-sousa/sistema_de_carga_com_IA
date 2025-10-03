package br.com.seunome.gerador;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GeradorApp {
    private static final String NOME_EXCHANGE = "imagens_exchange";
    // MUDANÇA AQUI: Caminho para as imagens DENTRO do container
    private static final String CAMINHO_BASE = "/data/test/";

    public static void main(String[] args) throws Exception {
        // ... (o resto do código continua exatamente igual)
        System.out.println("--> Iniciando Gerador...");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");

        Connection connection = null;
        while (connection == null) {
            try {
                connection = factory.newConnection();
            } catch (Exception e) {
                System.out.println("--> RabbitMQ ainda não está pronto. Tentando reconectar em 5 segundos...");
                Thread.sleep(5000);
            }
        }
        
        System.out.println("--> Conectado ao RabbitMQ!");

        try (Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(NOME_EXCHANGE, "topic");
            File pastaRostos = new File(CAMINHO_BASE + "rostos");
            File pastaTimes = new File(CAMINHO_BASE + "times");
            File[] tiposDeRosto = pastaRostos.listFiles(File::isDirectory);
            File[] tiposDeTime = pastaTimes.listFiles(File::isDirectory);
            Random random = new Random();
            System.out.println("--> Iniciando envio de imagens de TESTE. Pressione CTRL+C para sair.");

            while (true) {
                try {
                    String routingKey;
                    File imagemAleatoria;
                    if (random.nextBoolean() && tiposDeRosto != null && tiposDeRosto.length > 0) {
                        File pastaDeRosto = tiposDeRosto[random.nextInt(tiposDeRosto.length)];
                        File[] imagensNaPasta = pastaDeRosto.listFiles();
                        if (imagensNaPasta != null && imagensNaPasta.length > 0) {
                            imagemAleatoria = imagensNaPasta[random.nextInt(imagensNaPasta.length)];
                            routingKey = "face." + pastaDeRosto.getName();
                        } else { continue; }
                    } else if (tiposDeTime != null && tiposDeTime.length > 0) {
                        File pastaDeTime = tiposDeTime[random.nextInt(tiposDeTime.length)];
                        File[] imagensNaPasta = pastaDeTime.listFiles();
                        if (imagensNaPasta != null && imagensNaPasta.length > 0) {
                            imagemAleatoria = imagensNaPasta[random.nextInt(imagensNaPasta.length)];
                            routingKey = "team." + pastaDeTime.getName();
                        } else { continue; }
                    } else {
                        System.err.println("Nenhuma imagem de teste encontrada em " + CAMINHO_BASE);
                        TimeUnit.SECONDS.sleep(5);
                        continue;
                    }
                    Path path = imagemAleatoria.toPath();
                    byte[] bytesDaImagem = Files.readAllBytes(path);
                    String imagemBase64 = Base64.getEncoder().encodeToString(bytesDaImagem);
                    String mensagem = "{\"imagem_base64\": \"" + imagemBase64 + "\"}";
                    channel.basicPublish(NOME_EXCHANGE, routingKey, null, mensagem.getBytes("UTF-8"));
                    System.out.println(" [x] Enviada '" + routingKey + "' do arquivo de teste '" + imagemAleatoria.getName() + "'");
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (Exception e) {
                    System.err.println("Erro no loop do Gerador: " + e.getMessage());
                    e.printStackTrace();
                    TimeUnit.SECONDS.sleep(2);
                }
            }
        }
    }
}