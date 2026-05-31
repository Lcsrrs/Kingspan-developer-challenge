# Frontend — Kingspan Purchase Approval System

Interface web para gestão de solicitações de compra com fluxo de aprovação em múltiplos níveis.

---

## Tecnologias utilizadas

- **React 19**
- **Vite** — bundler e servidor de desenvolvimento
- **React Router DOM** — navegação entre páginas
- **Axios** — requisições HTTP
- **Tailwind CSS v4** — estilização

---

## Pré-requisitos

- Node.js 18 ou superior
- Backend rodando em `http://localhost:8080` (ver instruções em `../backend/README.md`)

---

## Configuração do ambiente

### 1. Instale as dependências

```bash
npm install
```

### 2. Configure a URL da API

Por padrão o frontend aponta para `http://localhost:8080`. Se o backend estiver em outra porta, edite o arquivo `src/services/api.js`:

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8080',
});
```

### 3. Suba o servidor de desenvolvimento

```bash
npm run dev
```

A aplicação estará disponível em `http://localhost:5173`.

---

## Usuários disponíveis para teste

Os usuários abaixo são criados automaticamente pelo backend na primeira execução:

| Nome | E-mail | Senha | Papel | Nível |
|---|---|---|---|---|
| Admin | admin@kingspan.com.br | admin | ADMIN | — |
| Aprovador1 | Aprovador1@kingspan.com.br | aprovador1 | APROVADOR | NIVEL_1 |
| Aprovador2 | Aprovador2@kingspan.com.br | aprovador2 | APROVADOR | NIVEL_2 |
| Aprovador3 | Aprovador3@kingspan.com.br | aprovador3 | APROVADOR | NIVEL_3 |
| Solicitante | solicitante@kingspan.com.br | solicitante | SOLICITANTE | — |

---

## Páginas e funcionalidades

### Login (`/login`)
- Autenticação com e-mail e senha
- Token JWT armazenado no `localStorage`
- Redirecionamento automático para o dashboard após login
- Redirecionamento para login ao acessar rotas protegidas sem token

### Dashboard (`/dashboard`)
- Listagem paginada de todas as solicitações
- Filtro por status (Pendente, Aprovada, Rejeitada, Cancelada)
- Filtro por nome do solicitante (busca parcial, case-insensitive)
- Botão de nova solicitação visível apenas para usuários com papel `SOLICITANTE`
- Navegação para o detalhe de cada solicitação

### Nova solicitação (`/new-request`)
- Formulário com título, descrição, valor e categoria
- Disponível apenas para `SOLICITANTE`
- Redireciona para o dashboard após criação

### Detalhe da solicitação (`/requests/:id`)
- Exibe todos os dados da solicitação e status atual
- Botões de ação contextuais exibidos conforme papel do usuário e estado atual:
  - **Aprovar** e **Rejeitar** — visíveis para `APROVADOR` e `ADMIN`
  - **Cancelar** — visível para o `SOLICITANTE` dono da solicitação e para `ADMIN`
  - Nenhum botão é exibido para solicitações em estado final (aprovada, rejeitada, cancelada)
- Campo de comentário opcional para todas as ações
- Histórico completo de ações com ator, transição de estado, comentário e data

---

## Estrutura do projeto

```
src/
  contexts/
    AuthContext.jsx      # Contexto de autenticação, Provider e hook useAuth
  services/
    api.js               # Instância do Axios com interceptor de token
    requestService.js    # Funções de acesso à API de solicitações
  pages/
    Login.jsx
    Dashboard.jsx
    NewRequest.jsx
    RequestDetail.jsx
  components/
    Navbar.jsx           # Barra de navegação com nome do usuário e logout
    PrivateRoute.jsx     # Proteção de rotas autenticadas
  App.jsx
  index.css
  main.jsx
```

---

## Decisões técnicas

### Autenticação

O token JWT retornado no login é armazenado no `localStorage` e injetado automaticamente em todas as requisições via interceptor do Axios. Ao receber um `401`, o interceptor remove o token e redireciona para a tela de login. O `AuthContext` verifica o token existente no `localStorage` na inicialização da aplicação e recupera os dados do usuário via `GET /auth/me`, mantendo a sessão ativa entre recarregamentos da página.

### Controle de acesso no frontend

Os botões de ação são renderizados condicionalmente com base no papel do usuário logado e no status atual da solicitação. Essa verificação é complementar à validação no backend — a regra de negócio real vive no servidor, e o frontend apenas adapta a interface para evitar ações inválidas desnecessárias.

### Filtro por solicitante

O filtro por nome do solicitante é aplicado no frontend sobre os dados já retornados pela API, usando comparação `includes` com ambos os lados em minúsculo para busca parcial e case-insensitive. O filtro por status é enviado como parâmetro de query para a API, que filtra no banco de dados.

### Paginação

A paginação é controlada pelo backend — o frontend envia os parâmetros `page` e `size` e exibe os controles de navegação com base no `totalPages` retornado na resposta.

---

## Relatório de desenvolvimento

### Dia 3 — Domingo

Desenvolvimento completo do frontend em paralelo com a finalização do backend. A stack escolhida foi React com Vite pela agilidade no setup, Tailwind CSS v4 pela praticidade na estilização sem necessidade de CSS customizado, e React Router para navegação entre páginas. O foco foi na funcionalidade e clareza da interface, priorizando o fluxo de aprovações e o feedback visual ao usuário em cada ação.