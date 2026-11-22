# Thay e Theu

ERP para gestão de clínicas odontológicas. O painel reúne pacientes, dentistas, agendamentos, prontuários e cobranças. A API Spring Boot usa PostgreSQL e autenticação JWT. O front-end usa React, Vite, React Router e Axios.

- `src/main/java` contém a API, organizada por módulo.
- `src/main/resources` contém a configuração do back-end.
- `src/test` contém os testes Maven.
- `frontend/src` contém as telas, estilos e cliente HTTP.

## Pré-requisitos

Instale JDK 25, Node.js 22.12 ou superior compatível com Vite, npm e PostgreSQL. As instruções abaixo usam PowerShell e os wrappers do repositório. No macOS ou Linux, use `./mvnw` e `npm`.

## Configuração local

No PostgreSQL, crie um banco vazio chamado `erp_clinica` se ainda não existir. Com o serviço ativo e as ferramentas PostgreSQL no PATH, execute `createdb -h localhost -U postgres erp_clinica`. O back-end lê variáveis do processo. Use [o exemplo da raiz](.env.example) como referência.

| Variável | Uso |
| --- | --- |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexão JDBC. Os padrões de URL e usuário são `jdbc:postgresql://localhost:5432/erp_clinica` e `postgres`. |
| `JWT_SECRET` | Chave aleatória em Base64 com pelo menos 32 bytes antes da codificação. |
| `JWT_EXPIRATION_MS` | Duração da sessão em milissegundos. Padrão de 24 horas. |
| `CORS_ALLOWED_ORIGINS` | Origens exatas separadas por vírgula. Em desenvolvimento são permitidas as portas Vite padrão de localhost e 127.0.0.1. |
| `DB_DDL_MODE` | Padrão local `update`. Para publicação, aplique o esquema em procedimento controlado e use `validate`. |
| `INITIAL_REGISTRATION_ENABLED` | Abre temporariamente a rota de cadastro do primeiro administrador. Padrão `false`. |
| `SWAGGER_ENABLED` | Habilita a documentação pública da API. Padrão `false`. |
| `VITE_API_URL` | URL pública do back-end, sem `/api`. É incorporada ao build. Use `frontend/.env.local` ou uma variável do processo. |

Na raiz, em um terminal:

```powershell
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Senha PostgreSQL' -AsSecureString)).Password
$jwtBytes = New-Object byte[] 32
$jwtGenerator = [Security.Cryptography.RandomNumberGenerator]::Create()
$jwtGenerator.GetBytes($jwtBytes)
$jwtGenerator.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
.\mvnw.cmd spring-boot:run
```

O comando acima gera uma chave nova por sessão de terminal. Mantenha uma chave exclusiva em um gerenciador de segredos se precisar preservar sessões entre reinícios.

Em outro terminal, na raiz:

```powershell
Set-Location frontend
npm.cmd ci
npm.cmd run dev
```

Abra `http://localhost:5173`. A API local responde em `http://localhost:8080`.

### Primeiro administrador

Somente em banco vazio, inicie temporariamente o back-end com `$env:INITIAL_REGISTRATION_ENABLED = 'true'` e `$env:SERVER_ADDRESS = '127.0.0.1'` definidos no terminal do back-end. Em outro terminal, execute o comando abaixo. Em seguida, reinicie o back-end com `INITIAL_REGISTRATION_ENABLED=false`.

```powershell
$admin = @{
  nome = Read-Host 'Nome do administrador'
  email = Read-Host 'E-mail do administrador'
  senha = [System.Net.NetworkCredential]::new('', (Read-Host 'Senha do administrador (mín. 8 caracteres)' -AsSecureString)).Password
}
Invoke-RestMethod -Uri 'http://127.0.0.1:8080/api/auth/cadastro-inicial' -Method Post -ContentType 'application/json' -Body (ConvertTo-Json $admin) | Out-Null
Remove-Variable admin
```

## Rotas principais

| Interface | API | Operações |
| --- | --- | --- |
| `/login` | `POST /api/auth/login` | Entrar e manter sessão conforme a opção Lembrar de mim. |
| `/dashboard` | Listagens abaixo | Contagens, próximos horários e pagamentos pendentes. |
| `/pacientes`, `/dentistas` | `/api/pacientes`, `/api/dentistas` | Listar ativos, cadastrar, consultar, editar e inativar com `DELETE /{id}`. |
| `/agendamentos` | `/api/agendamentos` | Listar, cadastrar, editar e atualizar status pelos sufixos `/confirmar`, `/concluir` e `/cancelar`. |
| `/prontuarios` | `/api/prontuarios` | Listar, cadastrar, consultar e editar. |
| `/pagamentos` | `/api/pagamentos` | Listar, cadastrar, consultar, editar pendências e dar baixa via `PATCH /{id}/pagar`. |

Cada módulo usa `/novo`, `/:id` e `/:id/editar` sob seu caminho. A API exige Bearer JWT nos módulos. A recuperação de senha depende de uma futura rota de back-end.

## Testes e build

Com o PostgreSQL de teste e as variáveis da API configurados, execute na raiz:

```powershell
.\mvnw.cmd verify
Set-Location frontend
npm.cmd run lint
npm.cmd run build
```

O teste Spring carrega a aplicação com as configurações informadas. Use um banco descartável para validações de cadastro, edição, inativação e pagamento.

## Preparação para publicação

1. Provisione o PostgreSQL, prepare e valide o esquema e configure `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_DDL_MODE=validate` e `JWT_SECRET` no ambiente do serviço.
2. Configure `CORS_ALLOWED_ORIGINS` com a origem HTTPS do painel. Mantenha `INITIAL_REGISTRATION_ENABLED=false` e `SWAGGER_ENABLED=false` no serviço público.
3. Gere o JAR com `mvnw verify` e execute-o com `java -jar target/erp-0.0.1-SNAPSHOT.jar`.
4. Defina `VITE_API_URL` com a URL HTTPS da API antes de `npm run build`. Sirva `frontend/dist` com fallback de páginas para `index.html` para suportar links diretos. Para uma única origem, encaminhe `/api` ao back-end.

Nunca publique credenciais, tokens, valores do `JWT_SECRET` ou arquivos `.env` preenchidos. Variáveis com prefixo `VITE_` são públicas.
