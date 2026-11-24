# Thindesk

O Thindesk é um projeto da disciplina para organizar o atendimento técnico. A ideia é acompanhar o chamado desde a abertura pelo usuário até o registro da solução pelo técnico. A aplicação usa Spring Boot, Thymeleaf e MongoDB.

## Demonstração

Para experimentar o sistema sem instalar nada, abra a [demonstração no Railway](https://thindesk-production.up.railway.app/login) e entre com a conta de teste.

- Usuário: `demo`
- Senha: `ThindeskDemo!2026`

A conta é compartilhada, então use dados fictícios nos chamados.

## Dashboard

![Tela inicial do Thindesk com menu lateral](docs/dashboard.png)

## Versão atual

A versão `1.0.1-beta`, definida no [pom.xml](pom.xml), marca a publicação da demonstração no Railway.

O projeto segue `MAJOR.MINOR.PATCH`. O primeiro número muda quando a API quebra compatibilidade, o segundo quando ganha uma funcionalidade compatível e o terceiro quando recebe uma correção compatível. O sufixo `-beta` indica um pré-lançamento. [SemVer 2.0.0](https://semver.org/lang/pt-BR/)

## Rodando na sua máquina

Você vai precisar do Java 17 e do MongoDB. Com o banco rodando, configure as variáveis de ambiente e inicie o projeto pelo PowerShell.

```powershell
$env:MONGODB_URI = "mongodb://localhost:27017/thindesk"
$env:ADMIN_USER = "admin"
$env:ADMIN_PASSWORD = "defina-uma-senha-forte"
.\mvnw.cmd spring-boot:run
```

Na primeira execução, o sistema cria a conta de administrador com o login e a senha definidos acima. Escolha uma senha própria com pelo menos 12 caracteres e entre em [localhost:8080/login](http://localhost:8080/login).

Para gerar o executável, rode `.\mvnw.cmd package`. O JAR fica em `target/thindesk-1.0.1-beta.jar`.

## Perfis de acesso

- `USUARIO` abre chamados e acompanha os que abriu.
- `TECNICO` acompanha a fila inteira e registra a solução dos chamados sem técnico ou atribuídos a ele.
- `ADMIN` também pode cadastrar contas, gerenciar clientes e ajustar os horários de atendimento.

## API REST

A API usa o mesmo login por sessão da interface web. Antes de enviar o formulário de login, busque o token em `GET /api/auth/csrf` e envie-o no campo `_csrf`. Depois de entrar, consulte a rota novamente e use o novo token no cabeçalho informado pela resposta ao fazer requisições de escrita.

| Método | Rota | Função |
|---|---|---|
| GET | /api/auth/csrf | Obter token CSRF para iniciar ou usar a sessão |
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

## Testes

O comando `.\mvnw.cmd test` roda os testes de autenticação, CSRF, validação e permissões com repositórios simulados. Para testar com o MongoDB, abra um chamado com uma conta de usuário, registre a solução com uma conta de técnico e confira se ela aparece ao consultar o chamado.

## Atualizando uma base existente

Se você já tem uma base do Thindesk, faça um backup antes de atualizar. Os campos `usuario` e `tecnico` dos chamados antigos precisam apontar para os logins correspondentes. O campo `versao` evita que duas atualizações concorrentes sobrescrevam a solução de um chamado. Para inicializá-lo nos registros antigos, execute o comando abaixo no `mongosh`.

```javascript
db.chamados.updateMany({ versao: { $exists: false } }, { $set: { versao: NumberLong(0) } })
```

Cada conta precisa de um login único, uma senha armazenada como hash BCrypt e um dos três perfis de acesso.
