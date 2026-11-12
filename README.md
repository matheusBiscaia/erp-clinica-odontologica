# ERP para Clínica Odontológica

API REST desenvolvida para gerenciar as principais operações de uma clínica odontológica.

## Funcionalidades

- Cadastro e gerenciamento de pacientes
- Cadastro e gerenciamento de dentistas
- Controle de agendamentos
- Validação de conflito de horários
- Registro de prontuários
- Controle de pagamentos
- Autenticação com JWT
- Proteção de rotas
- Documentação interativa com Swagger
- Tratamento centralizado de erros

## Tecnologias

- Java
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Hibernate
- Maven
- Swagger/OpenAPI
- Lombok

## Estrutura do projeto

```text
src/main/java/br/com/consultorio/erp
├── agendamento
├── autenticacao
├── config
├── dentista
├── exception
├── financeiro
├── paciente
└── prontuario
```

Cada módulo utiliza uma organização em camadas:

```text
controller → service → repository → database
```

## Requisitos

- Java instalado
- PostgreSQL
- Banco de dados `erp_clinica`
- Variáveis de ambiente configuradas

## Variáveis de ambiente

Use o arquivo `.env.example` como referência:

```env
DB_PASSWORD=
JWT_SECRET=
ADMIN_EMAIL=
ADMIN_PASSWORD=
```

Nunca envie o arquivo `.env` com valores reais para o GitHub.

## Executando o projeto

Clone o repositório:

```bash
git clone https://github.com/matheusBiscaia/erp-clinica-odontologica.git
cd erp-clinica-odontologica
```

Configure as variáveis de ambiente e execute:

```bash
export DB_PASSWORD="sua_senha_do_postgresql"
export JWT_SECRET="seu_segredo_jwt"
export ADMIN_EMAIL="email_do_administrador"
export ADMIN_PASSWORD="senha_do_administrador"

./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

## Documentação da API

Com o sistema em execução, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

## Autenticação

Faça login pelo endpoint:

```http
POST /api/auth/login
```

Exemplo:

```json
{
  "email": "admin@email.com",
  "senha": "sua_senha"
}
```

Copie o token retornado e envie nas rotas protegidas:

```http
Authorization: Bearer SEU_TOKEN
```

## Principais endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Realiza o login |
| GET/POST | `/api/pacientes` | Lista ou cadastra pacientes |
| GET/POST | `/api/dentistas` | Lista ou cadastra dentistas |
| GET/POST | `/api/agendamentos` | Lista ou cria agendamentos |
| GET/POST | `/api/prontuarios` | Lista ou cria prontuários |
| GET/POST | `/api/pagamentos` | Lista ou cria pagamentos |

## Testes manuais

Com a aplicação em execução e as credenciais carregadas:

```bash
chmod +x testar-api.sh
./testar-api.sh
```

O script testa:

- Login
- Rotas protegidas com token
- Bloqueio de acesso sem token

## Segurança

- Senhas armazenadas com hash
- Autenticação stateless
- Tokens JWT
- Rotas protegidas pelo Spring Security
- Credenciais fornecidas por variáveis de ambiente
- Arquivos sensíveis ignorados pelo Git

## Autor

**Matheus Biscaia**

- GitHub: [@matheusBiscaia](https://github.com/matheusBiscaia)