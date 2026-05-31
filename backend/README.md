# Backend — Kingspan Purchase Approval System

API REST para gestão de solicitações de compra com fluxo de aprovação em múltiplos níveis.

---

## Tecnologias utilizadas

- **Java 17**
- **Spring Boot 4**
- **Spring Security** — autenticação e autorização
- **Spring Data JPA + Hibernate** — mapeamento objeto-relacional
- **PostgreSQL** — banco de dados relacional
- **JWT (jjwt 0.12.6)** — autenticação stateless
- **Lombok** — redução de boilerplate
- **Maven** — gerenciamento de dependências

---

## Pré-requisitos

- Java 17 (JDK) instalado e configurado no `JAVA_HOME`
- PostgreSQL instalado e rodando localmente
- Maven (ou usar o wrapper `./mvnw` incluso no projeto)

---

## Configuração do ambiente

### 1. Crie o banco de dados

O Hibernate cria as tabelas automaticamente, mas o banco precisa existir antes de subir a aplicação:

```sql
CREATE DATABASE kingspan_db;
```

Execute via `psql`, pgAdmin ou qualquer cliente PostgreSQL de sua preferência.

### 2. Configure as variáveis de ambiente

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp .env.example .env
```

Conteúdo do `.env.example`:

```env
DB_URL=jdbc:postgresql://localhost:5432/kingspan_db
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_aqui
JWT_SECRET=uma_chave_secreta_com_pelo_menos_32_caracteres
JWT_EXPIRATION=86400000
```

> **Atenção:** o `JWT_SECRET` precisa ter no mínimo 32 caracteres para o algoritmo HS256 funcionar corretamente.

### 3. Suba a aplicação

```bash
./mvnw spring-boot:run -DskipTests
```

A aplicação estará disponível em `http://localhost:8080`.

Na primeira execução, os usuários padrão são criados automaticamente (ver seção abaixo).

---

## Usuários padrão

Criados automaticamente na inicialização via `DataSeeder`. Cobrem todos os papéis e níveis de aprovação do sistema:

| Nome | E-mail | Senha | Papel | Nível |
|---|---|---|---|---|
| Admin | admin@kingspan.com.br | admin | ADMIN | — |
| Aprovador1 | Aprovador1@kingspan.com.br | aprovador1 | APROVADOR | NIVEL_1 |
| Aprovador2 | Aprovador2@kingspan.com.br | aprovador2 | APROVADOR | NIVEL_2 |
| Aprovador3 | Aprovador3@kingspan.com.br | aprovador3 | APROVADOR | NIVEL_3 |
| Solicitante | solicitante@kingspan.com.br | solicitante | SOLICITANTE | — |

> Na segunda execução em diante o seeder verifica se o e-mail já existe antes de inserir, evitando duplicatas.

---

## Endpoints da API

### Autenticação

| Método | Rota | Descrição | Autenticação |
|---|---|---|---|
| POST | `/auth/register` | Cadastra novo usuário | Pública |
| POST | `/auth/login` | Autentica e retorna JWT | Pública |
| GET | `/auth/me` | Retorna dados do usuário logado | JWT |

### Solicitações

| Método | Rota | Descrição | Autenticação |
|---|---|---|---|
| POST | `/requests` | Cria nova solicitação | JWT — SOLICITANTE |
| GET | `/requests` | Lista solicitações paginadas | JWT |
| GET | `/requests/:id` | Detalha uma solicitação | JWT |
| PATCH | `/requests/:id/approve` | Aprova uma solicitação | JWT — APROVADOR/ADMIN |
| PATCH | `/requests/:id/reject` | Rejeita uma solicitação | JWT — APROVADOR/ADMIN |
| PATCH | `/requests/:id/cancel` | Cancela uma solicitação | JWT — SOLICITANTE dono/ADMIN |
| GET | `/requests/:id/history` | Histórico de ações da solicitação | JWT |

---

## Exemplos de requisição

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@kingspan.com.br",
    "password": "admin"
  }'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "name": "Admin",
  "email": "admin@kingspan.com.br",
  "role": "ADMIN"
}
```

### Criar solicitação

```bash
curl -X POST http://localhost:8080/requests \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Compra de notebooks",
    "description": "Aquisição de 5 notebooks para o time de TI",
    "amount": 15000.00,
    "category": "EQUIPMENT"
  }'
```

### Aprovar solicitação

```bash
curl -X PATCH http://localhost:8080/requests/{id}/approve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "comment": "Aprovado conforme orçamento disponível"
  }'
```

### Listar com filtro por status e paginação

```bash
curl "http://localhost:8080/requests?status=PENDING&page=0&size=10" \
  -H "Authorization: Bearer {token}"
```

### Erros de transição inválida (422)

Tentar aprovar uma solicitação já cancelada retorna:

```json
{
  "error": "INVALID_STATE_TRANSITION",
  "message": "Não é possível aprovar uma solicitação com status cancelled."
}
```

---

## Estrutura do projeto

```
src/main/java/com/kingspan/challenge/
  auth/                        # Autenticação e JWT
    dto/
      AuthResponseDTO.java
      LoginRequestDTO.java
      RegisterRequestDTO
    AuthController.java
    AuthService.java
  common/                      # Utilitários compartilhados
    security/
      SecurityConfiguration.java
      JwtAuthFilter.java
      JwtService.java
    exceptions/
      GlobalExceptionHandler.java
  history/                     # Histórico de ações
    dto/
      HistoryResponseDTO.java
    RequestHistory.java
    RequestHistoryRepository.java
    RequestHistoryService.java
  requests/                    # Solicitações de compra
    dto/
      ActionRequestDTO.java
      CreateRequestDTO.java
      PageResponseDTO.java
      RequestResponseDTO.java
    statemachine/              # Máquina de estados isolada
      StateMachineService.java
      InvalidStateTransitionException.java
    PurchaseRequest.java
    RequestStatus.java
    PurchaseRequestRepository.java
    PurchaseRequestService.java
    PurchaseRequestController.java
  users/                       # Entidade de usuário
    User.java
    UserRole.java
    ApproverLevel.java
    UserRepository.java
    DataSeeder.java
      
  GlobalExceptionHandler.java
    
```

---

## Decisões técnicas

### Linguagem e framework

Java foi escolhido pela familiaridade recente com a linguagem e por permitir a construção de um sistema estruturado e robusto. O Spring Boot foi adotado por ser o framework padrão do ecossistema Java para APIs REST, com suporte nativo a segurança, JPA e validação.

### Banco de dados

PostgreSQL foi escolhido pela familiaridade com o SGBD e por ser amplamente recomendado para aplicações relacionais em produção. O Hibernate gerencia o schema automaticamente via `ddl-auto=update`, eliminando a necessidade de migrations para o escopo do desafio.

### Autenticação

Spring Security com JWT stateless — sem sessão no servidor. Cada requisição carrega o token no header `Authorization: Bearer {token}`. O filtro `JwtAuthFilter` intercepta todas as requisições, valida o token e popula o `SecurityContext` com o usuário autenticado.

### Máquina de estados

Isolada em `StateMachineService` com responsabilidade única: validar se uma transição é permitida dado o estado atual, o papel do usuário e o nível de aprovação requerido. Lança `InvalidStateTransitionException` com mensagem descritiva em caso de transição inválida, capturada pelo `GlobalExceptionHandler` e retornada como HTTP 422.

### Níveis de aprovação

Calculados automaticamente no momento da criação da solicitação e persistidos no campo `required_level`. A regra:

- Até R$ 1.000,00 → NIVEL_1 (qualquer APROVADOR)
- Entre R$ 1.000,01 e R$ 10.000,00 → NIVEL_2 (APROVADOR sênior)
- Acima de R$ 10.000,00 → NIVEL_3 (somente ADMIN)

### Histórico

Todo evento que altera o estado de uma solicitação grava um registro em `request_history` com o ator, os estados anterior e posterior, comentário opcional e timestamp. Isso garante rastreabilidade completa do ciclo de vida de cada solicitação.

### Separação de responsabilidades

Controllers recebem e respondem requisições HTTP sem lógica de negócio. Services concentram as regras de negócio. Repositories isolam o acesso ao banco. DTOs garantem que nenhum campo sensível (como `passwordHash`) seja exposto nas respostas.

---

## Relatório de desenvolvimento

### Dia 1 — Quinta-feira à noite

Leitura do enunciado, desenvolvimento do modelo entidade-relacionamento e definição das etapas de implementação. Java foi escolhido como linguagem pela familiaridade recente e por permitir a construção de um sistema mais robusto e estruturado. O desenvolvimento contou com apoio supervisionado de inteligência artificial para acelerar decisões de arquitetura e resolver dúvidas pontuais de configuração.

### Dia 2 — Sexta-feira

Período da manhã e tarde dedicados a estudos: revisão de conceitos de CRUD, conexão com banco de dados via JPA e configuração do Spring Security. À noite, implementação das entidades JPA (`User`, `PurchaseRequest`, `RequestHistory`), configuração da conexão com PostgreSQL, implementação do `JwtService` e aproximadamente 70% das rotas de autenticação e segurança.

### Dia 3 — Sábado

Conclusão do backend: implementação de todas as rotas restantes (solicitações, aprovação, rejeição, cancelamento e histórico), desenvolvimento da máquina de estados com validações completas e retornos 422 descritivos, criação do `DataSeeder` com usuários padrão para todos os papéis e níveis, e revisão geral das regras de negócio.

