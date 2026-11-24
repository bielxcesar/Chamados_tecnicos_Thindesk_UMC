# Thindesk — chamados técnicos

Aplicação Spring Boot com MongoDB, Thymeleaf e autenticação por sessão.

## Versionamento

A versão atual é **`1.0.0-alpha`**, definida no [`pom.xml`](pom.xml). Esta prévia da versão 1.0.0 inclui abertura, consulta e resolução de chamados, autenticação por sessão, validação de dados e permissões por perfil.

O projeto segue o formato `MAJOR.MINOR.PATCH[-pré-lançamento]`.

| Parte | Quando muda |
|---|---|
| `MAJOR` (maior) | Mudanças incompatíveis na API documentada |
| `MINOR` (menor) | Funcionalidades novas que preservam a compatibilidade |
| `PATCH` (correção) | Correções que preservam a compatibilidade |
| `alpha` (pré-lançamento) | Identifica uma versão preliminar anterior à versão estável |

[Especificação do Versionamento Semântico 2.0.0](https://semver.org/lang/pt-BR/)

## Dashboard

Tela inicial do Thindesk após a autenticação, com o menu de navegação da aplicação.

![Dashboard do Thindesk com menu lateral e tela de boas-vindas](docs/dashboard.png)

## Executar

Requer JDK 17 ou superior, MongoDB e acesso ao Maven Central na primeira compilação.
No PowerShell, dentro da pasta do projeto:

```powershell
$env:MONGODB_URI = "mongodb://localhost:27017/thindesk"
$env:ADMIN_USER = "admin"
$env:ADMIN_PASSWORD = "SUA-SENHA-FORTE-AQUI"
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Abra http://localhost:8080/login. O administrador inicial é criado apenas quando
ADMIN_PASSWORD foi informado e o login ainda não existe. Não há senha padrão.
Após criar a conta, remova ADMIN_PASSWORD do ambiente. Alterar a variável não
redefine a senha de contas existentes. O arquivo .env.example é apenas referência;
Spring Boot não carrega um arquivo .env automaticamente.

O empacotamento foi padronizado para JAR executável (antes WAR com dependências
Java EE antigas): `./mvnw package`, seguido de `java -jar target/thindesk-1.0.0-alpha.jar`.

## Permissões

| Ação | USUARIO | TECNICO | ADMIN |
|---|---|---|---|
| Abrir chamado em seu nome | Sim | Sim | Sim |
| Consultar próprios chamados | Sim | Sim | Sim |
| Consultar chamados de outros | Não | Sim | Sim |
| Resolver chamado sem técnico ou atribuído a si | Não | Sim | Sim |
| Resolver chamado atribuído a outro técnico | Não | Não | Não |
| Cadastrar contas e definir perfis | Não | Não | Sim |
| Administrar clientes e horários | Não | Não | Sim |

A página /chamados permite abertura e resolução com as mesmas regras da API.
O usuário e o técnico são obtidos da sessão, nunca do JSON. Não existe cadastro
público: somente o administrador pode criar contas. Senhas são armazenadas com
BCrypt e não aparecem nas respostas. A listagem de usuários comuns é filtrada
pelo login autenticado, inclusive na interface HTML.

## Usar a API pelo PowerShell

A API usa o mesmo login por sessão do navegador e mantém CSRF habilitado.
O exemplo abaixo autentica, cria um usuário e abre um chamado. Substitua senhas.

```powershell
$base = "http://localhost:8080"
$csrf = Invoke-RestMethod "$base/api/auth/csrf" -SessionVariable sessao
Invoke-WebRequest "$base/login" -Method Post -WebSession $sessao -Body @{
    username = "admin"; password = "SUA-SENHA-FORTE-AQUI"; _csrf = $csrf.token
}
# O token é renovado após o login. Busque-o novamente.
$csrf = Invoke-RestMethod "$base/api/auth/csrf" -WebSession $sessao
$headers = @{}; $headers[$csrf.headerName] = $csrf.token
Invoke-RestMethod "$base/api/auth/me" -WebSession $sessao
Invoke-RestMethod "$base/api/usuarios" -Method Post -WebSession $sessao -Headers $headers -ContentType "application/json; charset=utf-8" -Body (@{
    login="tecnico1"; senha="OUTRA-SENHA-FORTE-AQUI"; perfil="TECNICO"
} | ConvertTo-Json)
Invoke-RestMethod "$base/api/chamados" -Method Post -WebSession $sessao -Headers $headers -ContentType "application/json; charset=utf-8" -Body (@{
    titulo="Computador sem rede"; descricao="A conexão caiu no laboratório"; tipo="Rede"
} | ConvertTo-Json)
# Para resolver, entre como técnico/admin e use o ID retornado:
# PUT /api/chamados/{id}/resolver — JSON: {"solucao":"Cabo substituído"}
```

### Rotas

| Método | Rota | Função |
|---|---|---|
| GET | /api/auth/csrf | Obter token CSRF (público; não dá acesso a dados) |
| POST | /login | Autenticar com formulário username/password/_csrf |
| POST | /logout | Encerrar sessão (exige CSRF) |
| GET | /api/auth/me | Identificar sessão |
| POST | /api/usuarios | Criar conta (ADMIN) |
| GET/POST | /api/chamados | Listar/abrir chamados |
| GET | /api/chamados/{id} | Consultar chamado com controle de acesso |
| GET | /api/chamados/usuario/{login} | Consultar chamados por login |
| PUT | /api/chamados/{id}/resolver | Registrar solução e técnico autenticado |
| GET/POST | /api/clientes | Listar/criar clientes (ADMIN) |
| GET/POST | /api/ajustes-horarios | Listar/salvar horários (ADMIN) |
| DELETE | /api/ajustes-horarios/{id} | Excluir horário (ADMIN) |

Os DTOs limitam campos e tamanhos. Título, descrição, tipo e solução não podem
ser vazios. Horários usam HH:mm e o fim deve ser posterior ao início.
Erros: 400 para dados inválidos, 401 para API sem sessão, 403 para falta de
permissão ou CSRF, 404 para chamado ausente e 409 para conflito. Requisições de
escrita sem CSRF podem retornar 403 antes da verificação do login.

## Dados existentes

Faça backup antes de usar esta versão com uma base anterior. O campo `usuario`
dos chamados deve conter o login da conta proprietária; o campo `tecnico`, quando
preenchido, deve corresponder ao login do técnico responsável. Os antigos IDs ou
nomes livres precisam ser mapeados manualmente para os logins corretos.

Chamados agora usam controle de versão para evitar sobrescrever resoluções
concorrentes. Antes de iniciar com uma base antiga, no mongosh:

```javascript
use thindesk
db.chamados.updateMany({versao: {$exists: false}}, {$set: {versao: NumberLong(0)}})
```

Contas antigas sem senha BCrypt e perfil USUARIO/TECNICO/ADMIN não são compatíveis.
O índice único de login exige remover duplicidades antes da inicialização.
Nenhum script deste projeto apaga ou migra automaticamente dados existentes.

## Verificação e apresentação ao professor

Execute `./mvnw test`. Os testes de segurança usam MockMvc e repositórios simulados;
não exigem MongoDB. Eles verificam login correto e senha incorreta, acesso anônimo, CSRF, validação de campos,
permissão administrativa, isolamento entre usuários, resolução por técnico,
identidade do solicitante e bloqueio de resolução repetida ou por outro técnico.

A compilação e os 12 testes passaram com JDK 17. A verificação automatizada usa repositórios simulados. Para validar a persistência, execute o roteiro abaixo com MongoDB ativo.

Roteiro manual: crie duas contas USUARIO e uma TECNICO; abra um chamado com cada
usuário; confirme que um usuário não lê o chamado do outro; tente resolver como
usuário (403); resolva como técnico; tente resolver novamente (409). Confira no
MongoDB que a senha é um hash e que técnico/solicitante correspondem à sessão.

Referências de implementação: [autorização](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html)
e [CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html).

Para publicação, use HTTPS e configure `server.servlet.session.cookie.secure=true`.
