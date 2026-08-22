# Food API

API REST para gerenciamento de usuários de um sistema de restaurante, desenvolvida com Spring Boot seguindo abordagem **API First**.

## Tecnologias

- **Java 21**
- **Spring Boot 3.5.16**
    - Spring Web
    - Spring Data JPA
    - Spring Security
    - Bean Validation
- **MySQL 8**
- **JWT**
- **OpenAPI Generator**
- **Springdoc OpenAPI / Swagger UI**
- **Maven**
- **Docker & Docker Compose**

## Como rodar com Docker (recomendado)

### Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose instalados

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/tech-challengers/phase-1-spring-explorer.git
   cd phase-1-spring-explorer
   ```

2. Suba os containers (aplicação + banco de dados):
   ```bash
   docker compose up --build
   ```

3. Aguarde os logs indicarem que a aplicação subiu (`Started FoodApiApplication`). As tabelas são criadas/atualizadas automaticamente pelo Hibernate (`ddl-auto: update`) no primeiro start.

4. Acesse a documentação interativa (Swagger UI):
   ```
   http://localhost:8080/swagger
   ```

Para derrubar os containers:
```bash
docker compose down
```

Para derrubar e apagar também os dados do banco:
```bash
docker compose down -v
```

### Serviços

| Serviço | Container | Porta | Descrição |
|---------|-----------|-------|-----------|
| `mysql` | `mysql-tech-challenge` | 3306 | Banco de dados MySQL 8 |
| `app`   | `spring-tech-challenge` | 8080 | API Spring Boot |

## Como rodar localmente (sem Docker)

### Pré-requisitos

- Java 21
- Maven
- MySQL 8

### Passos

1. Crie o banco de dados:
   ```sql
   CREATE DATABASE restaurant_db;
   ```

2. Configure suas credenciais em `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/restaurant_db
       username: root
       password: sua_senha
   ```

3. Rode a aplicação:
   ```bash
   mvn spring-boot:run
   ```

4. Acesse `http://localhost:8080/swagger`

## Endpoints principais

| Método | Rota | Autenticação | Descrição |
|--------|------|--------------|-----------|
| POST   | `/api/v1/users` | Não | Cadastrar usuário |
| GET    | `/api/v1/users?nome=` | Sim | Buscar usuários por nome |
| GET    | `/api/v1/users/{userId}` | Sim | Buscar usuário por ID |
| PUT    | `/api/v1/users/{userId}` | Sim | Atualizar dados do usuário |
| DELETE | `/api/v1/users/{userId}` | Sim | Excluir usuário |
| PATCH  | `/api/v1/users/{userId}/senha` | Sim | Alterar senha do usuário |
| POST   | `/api/v1/auth/login` | Não | Autenticar e obter token JWT |

Consulte o contrato completo (schemas de request/response, códigos de erro) na própria documentação Swagger.

## Autenticação

A API usa **JWT** para proteger a maioria das rotas. Para testar endpoints protegidos:

1. Faça login em `POST /api/v1/auth/login` com um usuário já cadastrado, informando login e senha.
2. Copie o `token` retornado na resposta.
3. No Swagger UI, clique no botão **Authorize** (canto superior direito) e cole o token no formato:
   ```
   Bearer <seu-token-aqui>
   ```
4. Clique em **Authorize** e depois em **Close**. Todas as chamadas seguintes vão incluir o token automaticamente.

Rotas sem autenticação: cadastro de usuário (`POST /api/v1/users`) e login (`POST /api/v1/auth/login`).

## Estrutura do projeto

```
src/main/java/br/com/foodapi/
├── config/            # Configurações gerais
├── controller/        # Controllers da aplicação
│   ├── AbstractController
│   ├── AuthController    # Login / geração de token JWT
│   ├── ControllerAdvice  # Tratamento global de exceções
│   └── UserController
├── domain/
│   ├── factory/        # Factories para criação de objetos de domínio
│   └── model/           # Entidades de domínio
├── infra/
│   ├── errors/          # Exceções customizadas
│   └── security/        # Configuração do Spring Security e JWT
├── repository/
│   └── UserRepository
├── service/
│   ├── CustomUserDetailsService  # Integração do Spring Security com os usuários
│   ├── JwtService                # Geração e validação de tokens JWT
│   └── UserService
└── generated/          # Código gerado a partir do contrato OpenAPI
```

## Testes

O projeto conta com testes automatizados em `src/test/java/br/com/foodapi`:

| Classe | Camada | O que cobre |
|--------|--------|-------------|
| `UserServiceTest` | Service | Testes unitários (Mockito) da lógica de negócio de `UserService`, isolando `UserRepository` e `PasswordEncoder` via `@Mock`/`@InjectMocks` |
| `ControllerAdviceTest` | Controller | Testes de integração (`@WebMvcTest`) do tratamento global de exceções via `ControllerAdvice` |
| `TestController` / `TestRequest` | Suporte | Controller e DTO fictícios (rotas `/test/exception`, `/test/validation`) usados para disparar exceções de forma controlada nos testes do `ControllerAdviceTest` |

**Cenários cobertos pelo `UserServiceTest`:**

- Cadastro de cliente com e-mail/login válidos → cria instância de `Cliente`
- Cadastro de dono de restaurante com e-mail/login válidos → cria instância de `DonoRestaurante`
- Cadastro com e-mail já existente → `UserAlreadyExistsException`
- Cadastro com login já existente → `UserAlreadyExistsException`
- Exclusão de usuário existente
- Exclusão de usuário inexistente → `UserNotFoundException`
- Troca de senha de usuário inexistente → `UserNotFoundException`
- Troca de senha com senha atual incorreta → `InvalidPasswordException`
- Troca de senha com nova senha igual à atual → `InvalidPasswordException`
- Troca de senha bem-sucedida (verifica encode/matches do `PasswordEncoder` e persistência)

**Cenários cobertos pelo `ControllerAdviceTest`:**

- Usuário já cadastrado → `409 Conflict`
- Corpo de requisição inválido (falha de `@Valid`) → `400 Bad Request`
- Busca de usuário por ID com sucesso → `200 OK`
- Usuário não encontrado → `404 Not Found`
- Senha atual incorreta na troca de senha → `400 Bad Request`
- Nova senha igual à atual na troca de senha → `400 Bad Request`

### Rodando os testes

Via Maven:
```bash
mvn test
```

Ou diretamente pela IntelliJ, clicando no ▶️ ao lado de cada classe de teste (ou da pasta `src/test/java` inteira para rodar todos de uma vez).

## Usuários de teste (seed)

Ao subir a aplicação pela primeira vez, o `DatabaseSeeder` cria automaticamente dois usuários para facilitar os testes (login em `POST /api/v1/auth/login`):

| Login | Senha | Tipo |
|-------|-------|------|
| `fulano` | `12345678` | Dono de restaurante |
| `siclano` | `12345678` | Cliente |
