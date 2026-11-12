# Guia de acesso ao Sistema de Gestão de Bilhetes

## 1. Requisitos

- JDK 17 ou superior.
- PostgreSQL instalado e em execução.
- Banco de dados `sistema_bilhetes` com as tabelas do projecto.
- Driver JDBC do PostgreSQL adicionado às dependências do módulo no IntelliJ.

## 2. Configurar a ligação ao banco

No ficheiro `src/database/DatabaseConnection.java`, configura:

```java
private static final String URL =
        "jdbc:postgresql://localhost:5432/sistema_bilhetes";

private static final String USER = "postgres";

private static final String PASSWORD = "SUA_SENHA_POSTGRES";
```

Substitui `SUA_SENHA_POSTGRES` pela senha do teu PostgreSQL.

As credenciais do banco são diferentes das contas usadas para entrar na aplicação.

## 3. Criar as contas de demonstração

Liga-te ao banco pelo terminal:

```bash
psql -h localhost -U postgres -d sistema_bilhetes
```

Executa o SQL abaixo para criar um administrador e um promotor activos:

```sql
INSERT INTO usuarios (nome, email, senha, tipo, estado)
SELECT
    'Administrador',
    'admin@eventos.com',
    'Admin123',
    'ADMIN',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios
    WHERE email = 'admin@eventos.com'
);

INSERT INTO usuarios (nome, email, senha, tipo, estado)
SELECT
    'Promotor de Demonstração',
    'promotor@eventos.com',
    'Promotor123',
    'PROMOTOR',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios
    WHERE email = 'promotor@eventos.com'
);
```

Se um email já existir, este SQL mantém a conta existente e não altera a sua senha.

> Estas credenciais destinam-se à demonstração local. A implementação actual guarda senhas em texto simples.

## 4. Credenciais de demonstração

| Perfil | Email | Senha |
|  | --- | --- |
| Administrador | admin@eventos.com | Admin123 |
| Promotor | promotor@eventos.com | Promotor123 |
| Cliente | valter@gmail.com | valter |
| Cliente | Email indicado no cadastro | Senha indicada no cadastro |

## 5. Iniciar o sistema

1. Abre o projecto no IntelliJ.
2. Confirma que o JDK está configurado.
3. Em **Project Structure → Modules → Dependencies**, adiciona o JAR do PostgreSQL com **Scope: Compile**.
4. Executa a classe `Main`.
5. Introduz o email e a senha na tela de login.
6. Clica em **ENTRAR**.

## 6. Acesso por perfil

### Administrador

Depois de entrar, pode:

- Consultar, criar, actualizar, activar e desactivar clientes e promotores.
- Remover utilizadores, quando os relacionamentos no banco permitirem.
- Consultar os eventos.
- Consultar as estatísticas de vendas.

### Promotor

1. Entra com a conta de promotor.
2. Na aba **Eventos**, preenche os dados e clica em **Criar**.
3. Selecciona o evento na tabela.
4. Abre **Bilhetes do evento**.
5. Escolhe o tipo **NORMAL** ou **VIP**.
6. Define o preço e a quantidade.
7. Clica em **Criar bilhetes**.
8. Consulta as vendas na aba **Vendas**.

A data e hora do evento devem ser futuras.

### Cliente

1. Na tela de login, clica em **CADASTRAR**.
2. Preenche nome, email e senha com pelo menos quatro caracteres.
3. Conclui o cadastro e faz o login.
4. Selecciona um evento.
5. Selecciona um bilhete disponível.
6. Indica a quantidade e confirma a compra.
7. Consulta o histórico em **Minhas compras → Actualizar histórico**.

A compra regista a venda e reduz o stock. Não existe integração com um serviço de pagamento.

## 7. Problemas frequentes

| Mensagem ou situação | O que verificar |
| --- | --- |
| `No suitable driver found` | Adicionar o JAR do PostgreSQL ao módulo usado para executar `Main`. |
| `Connection refused` | Confirmar que o PostgreSQL está em execução no endereço e porta configurados. |
| Falha de autenticação do PostgreSQL | Conferir o utilizador e a senha em `DatabaseConnection`. |
| Banco ou tabela inexistente | Criar o banco e executar o script de estrutura do projecto. |
| Login sem sucesso | Conferir email, senha e se a conta está activa. |
| Cadastro sem sucesso | Verificar se o email já existe e consultar o erro no console. |
| Nenhum evento disponível | Criar um evento activo com data futura. |
| Evento sem bilhetes disponíveis | Criar bilhetes activos com stock positivo para esse evento. |

## 8. Limitações pendentes

- O histórico precisa de usar o `valor_total` guardado no banco, em vez de recalcular o preço actual.
- As regras de capacidade e disponibilidade precisam de validação transaccional na camada de negócio.
- A compilação e o funcionamento completo devem ser verificados no ambiente local.