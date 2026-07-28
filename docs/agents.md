# Especificação de PoC: Sistema de Observabilidade (Loja + Collector)

## 1. Visão Geral do Projeto
O objetivo deste projeto é criar uma Prova de Conceito (PoC) local para validar uma arquitetura de observabilidade. O sistema será composto por uma infraestrutura baseizada em contêineres e dois microsserviços Spring Boot que se comunicarão via RabbitMQ. 

O fluxo principal consiste em um serviço produtor (`loja-service`) que emite métricas de negócio para uma fila, e um serviço consumidor (`collector-service`) que processa essas mensagens em lote (batching) e as envia para o Elasticsearch para posterior visualização no Grafana.

O projeto adotará uma estrutura de **Monorepo**, dividindo os serviços em diretórios distintos na raiz do repositório.

## 2. Estrutura do Monorepo
A estrutura de diretórios esperada é a seguinte:

```text
/observability-poc
  ├── docker-compose.yml
  ├── loja-service/        (Spring Boot + Thymeleaf)
  └── collector-service/   (Spring Boot + Elasticsearch Client)
```

## 3. Infraestrutura Base (Docker Compose)
Na raiz do monorepo, deve existir o arquivo `docker-compose.yml` para orquestrar os serviços essenciais.

**Requisitos do `docker-compose.yml`:**
- **Elasticsearch:** Versão 8.12.0. Deve rodar como `single-node` com a segurança (xpack) desativada para facilitar testes locais. Porta: 9200.
- **RabbitMQ:** Imagem com a interface de gerenciamento (`rabbitmq:3-management`). Portas: 5672 (broker) e 15672 (UI).
- **Grafana:** Imagem `latest`. Deve depender do Elasticsearch. Porta: 3000.

## 4. Serviço 1: Loja (Produtor)
O `loja-service` simula a interação do usuário e atua como o produtor das métricas.

**Tecnologias:** Java, Spring Boot, Spring Web, Thymeleaf, Spring for RabbitMQ.
**Porta:** 8080.

**Requisitos de Implementação:**
- **Página Web:** Uma interface simples em Thymeleaf com três botões que simulam ações do usuário: "Visualizar Produto", "Adicionar ao Carrinho" e "Finalizar Compra".
- **Comunicação com RabbitMQ:** O serviço deve enviar uma mensagem para a fila `metrics.queue` no broker RabbitMQ local.
- **Formato da Mensagem:** A mensagem deve conter os seguintes campos (pode ser um `Map` ou um DTO simples):
  - `service`: "loja-frontend"
  - `action`: "view_product", "add_to_cart" ou "checkout"
  - `timestamp`: epoch atual em milissegundos.
  - `value`: Valor double (ex: 150.0 se for "checkout", 0.0 caso contrário).

## 5. Serviço 2: Collector (Consumidor)
O `collector-service` atua como um agente de ingestão, agrupando as métricas e enviando-as ao banco de dados em lotes.

**Tecnologias:** Java, Spring Boot, Spring for RabbitMQ, Elasticsearch Java Client (8.12.0).
**Porta:** 8081.

**Requisitos de Implementação:**
- **Listener:** Deve escutar a fila `metrics.queue` do RabbitMQ.
- **Estratégia de Batching:** 
  - O serviço não deve enviar cada mensagem individualmente ao Elasticsearch.
  - As mensagens devem ser armazenadas em um buffer em memória.
  - O envio (flush) via Bulk API do Elasticsearch deve ocorrer através de uma estratégia mista: **Time-based e Size-based**. 
  - Deve enviar o lote assim que atingir um tamanho máximo (ex: 100 mensagens) **OU** em intervalos regulares de tempo (ex: a cada 5 segundos usando `@Scheduled(fixedRate = 5000)`), o que ocorrer primeiro.
- **Destino:** As métricas devem ser indexadas no Elasticsearch em um índice com o padrão `metrics-*` (ex: `metrics-poc`).

## 6. Instruções para o Agente (IA)
Com base nesta especificação, atue como um engenheiro de software e realize as seguintes tarefas:

1. Gere o arquivo `docker-compose.yml` completo com a infraestrutura descrita.
2. Forneça o código essencial do `loja-service`, incluindo a configuração do RabbitMQ (como criar a fila, se necessário), o Controller e a página HTML (Thymeleaf).
3. Forneça o código do `collector-service`, com atenção especial à implementação robusta da estratégia de batching (Size + Time) e a integração com o `ElasticsearchClient`.
4. (Opcional) Forneça os arquivos `pom.xml` resumidos, focando nas dependências cruciais.