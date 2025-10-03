# Sistema de Carga com IA embutida nos Consumidores, Containers e RabbitMQ

## 📌 Descrição
Este projeto implementa um **sistema distribuído** em **Java** com suporte a **Docker** e **RabbitMQ**.  
O sistema simula a geração e o consumo de mensagens contendo **rostos humanos** ou **brasões de times de futebol**, processados por diferentes consumidores que utilizam **IA com a biblioteca Smile**.

## ⚙️ Arquitetura do Sistema
O sistema é composto por **4 containers**:

1. **Gerador de Mensagens**  
   - Produz mensagens a uma taxa de **5 por segundo ou mais**.  
   - Mensagens de dois tipos:  
     - `face` → Imagem de rosto humano.  
     - `team` → Imagem de brasão de time de futebol.  
   - Publica mensagens no RabbitMQ com **routing keys** adequadas.  

2. **RabbitMQ**  
   - Atua como **broker de mensagens**.  
   - Usa **Topic Exchange** para rotear mensagens para os consumidores.  
   - Painel de administração habilitado em `http://localhost:15672` (usuário: `guest` / senha: `guest`).  

3. **Consumidor 1 – Análise de Sentimentos (Faces)**  
   - Recebe mensagens com `routing key = face`.  
   - Processa imagens de rostos com IA (Smile).  
   - Detecta **emoções** (feliz, triste, bravo etc.).  
   - Intencionalmente mais lento que o gerador → fila cresce.  

4. **Consumidor 2 – Reconhecimento de Times (Teams)**  
   - Recebe mensagens com `routing key = team`.  
   - Processa brasões de clubes de futebol com IA (Smile).  
   - Identifica o **time de futebol**.  
   - Também mais lento que o gerador.  

### 🖼️ Estrutura em Containers
```
+------------------+        +------------------+
|   Gerador        | -----> |    RabbitMQ      |
+------------------+        +------------------+
                                 |       |
                                 v       v
                       +------------------+   +------------------+
                       | Consumidor Face  |   | Consumidor Team  |
                       +------------------+   +------------------+
```

## 🐳 Estrutura do Projeto
```
sistema-carga-ia/
├── docker-compose.yml
├── Consumidor1.Dockerfile
├── Consumidor2.Dockerfile
├── Gerador.Dockerfile
├── pom.xml
└── src/
    └── main/java/br/com/seunome/
        ├── consumidor1/ConsumidorRostoApp.java
        ├── consumidor2/ConsumidorTimeApp.java
        └── gerador/GeradorApp.java
```

## 🚀 Como Executar

### Pré-requisitos
- **Docker** e **Docker Compose** instalados  
- **Java 17+** e **Maven** (caso queira compilar/testar fora do Docker)  

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/SEU_USUARIO/sistema-carga-ia.git
   cd sistema-carga-ia
   ```

2. Construa e inicie os containers:
   ```bash
   sudo docker-compose up --build
   ```

3. Acesse o painel do RabbitMQ:  
   👉 [http://localhost:15672](http://localhost:15672)  
   - Usuário: `guest`  
   - Senha: `guest`  

4. Verifique os logs dos consumidores:
   ```bash
   docker logs -f consumidor-face
   docker logs -f consumidor-team
   ```

## 📊 Comportamento Esperado
- O **Gerador** produz mensagens mais rápido do que os consumidores processam.  
- As filas do **RabbitMQ aumentam visivelmente**.  
- O **Consumidor Face** mostra análises de emoções (feliz, triste, bravo).  
- O **Consumidor Team** mostra o time de futebol identificado.  

## 🛠️ Tecnologias Utilizadas
- **Java 17**  
- **Maven**  
- **RabbitMQ**  
- **Smile (IA e Machine Learning)**  
- **Docker & Docker Compose**  

## 👥 Autores
- James Sousa  
- David Natan
- Vanderlei Carvalho 
