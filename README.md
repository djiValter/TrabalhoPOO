# 🎟️ Sistema de Gestão de Bilhetes de Eventos

<p align="center">
  <strong>Uma aplicação desktop para gerir eventos, bilhetes e utilizadores de forma simples e organizada.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Java%20Swing-GUI-blue?style=for-the-badge" alt="Java Swing"/>
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-orange?style=for-the-badge" alt="Estado do projeto"/>
</p>

---

## 📖 Sobre o Projeto

O **Sistema de Gestão de Bilhetes de Eventos** é um projeto académico desenvolvido em Java, com o objetivo de facilitar a gestão de eventos e a comercialização de bilhetes através de uma interface gráfica desktop.

A aplicação permite organizar eventos, gerir bilhetes e controlar o acesso às funcionalidades de acordo com o perfil de cada utilizador.

O projeto aplica conceitos fundamentais da Programação Orientada a Objetos (POO), incluindo encapsulamento, herança, abstração, polimorfismo e interfaces, utilizando Java Swing para a interface gráfica e PostgreSQL para a persistência de dados.

## 🎯 Objetivos

- Desenvolver uma aplicação desktop para gestão de eventos.
- Aplicar os princípios da Programação Orientada a Objetos.
- Implementar diferentes níveis de acesso para os utilizadores.
- Facilitar a criação e gestão de eventos e bilhetes.
- Armazenar e gerir informações numa base de dados relacional.

## ✨ Funcionalidades

### 👤 Cliente
- Criar conta e iniciar sessão.
- Consultar eventos disponíveis.
- Visualizar informações dos eventos.
- Consultar os bilhetes disponíveis.
- Comprar bilhetes para eventos.

### 🎤 Promotor
- Iniciar sessão na plataforma.
- Criar e cadastrar eventos.
- Consultar e atualizar eventos.
- Gerir os bilhetes associados aos seus eventos.
- Consultar informações relevantes sobre os eventos.

### 🛡️ Administrador
- Gerir utilizadores do sistema.
- Gerir clientes e promotores.
- Consultar e administrar eventos.
- Gerir bilhetes e informações da plataforma.

> **Nota:** As funcionalidades acima representam o âmbito previsto do sistema. Algumas poderão estar ainda em desenvolvimento.

## 🧠 Conceitos de Programação Aplicados

| Conceito | Aplicação no projeto |
|---|---|
| Encapsulamento | Proteção dos atributos das classes. |
| Herança | Especialização de classes e reutilização de código. |
| Abstração | Definição das características essenciais dos objetos. |
| Polimorfismo | Comportamentos diferentes através de uma interface comum. |
| Interfaces | Definição de contratos para operações do sistema. |
| JDBC | Comunicação entre Java e PostgreSQL. |
| DAO | Separação do acesso aos dados da lógica da aplicação. |

## 🛠️ Tecnologias Utilizadas

- **Java** — linguagem de programação.
- **Java Swing** — desenvolvimento da interface gráfica.
- **PostgreSQL** — sistema de gestão de base de dados.
- **JDBC** — ligação à base de dados.
- **IntelliJ IDEA** — ambiente de desenvolvimento.
- **Git e GitHub** — controlo de versões e colaboração.

## 🗂️ Estrutura do Projeto

A organização pode variar de acordo com a implementação atual, mas uma estrutura recomendada é:

```text
Sistema-Bilhetes-Eventos/
├── src/
│   ├── model/
│   │   ├── Usuario.java
│   │   ├── Cliente.java
│   │   ├── Promotor.java
│   │   ├── Admin.java
│   │   ├── Evento.java
│   │   ├── Bilhete.java
│   │   ├── BilheteNormal.java
│   │   └── BilheteVIP.java
│   ├── dao/
│   │   ├── UsuarioDAO.java
│   │   └── BilheteDAO.java
│   ├── database/
│   │   └── DatabaseConnection.java
│   ├── service/
│   └── view/
├── database/
│   └── schema.sql
├── .gitignore
└── README.md
```

*Esta árvore é ilustrativa. Ajusta os nomes e as pastas à estrutura real do teu repositório.*

## ⚙️ Pré-requisitos

Antes de executar a aplicação, certifica-te de que tens instalado:

- Java JDK.
- PostgreSQL.
- IntelliJ IDEA ou outra IDE compatível com Java.
- Driver JDBC do PostgreSQL.

## 🚀 Instalação e Execução

### 1. Clonar o repositório

```bash
git clone https://github.com/TEU-UTILIZADOR/TEU-REPOSITORIO.git
```

Entra na pasta do projeto:

```bash
cd TEU-REPOSITORIO
```

### 2. Configurar a base de dados

Cria uma base de dados PostgreSQL com o nome utilizado pelo projeto.

Exemplo:

```sql
CREATE DATABASE sistema_bilhetes;
```

Executa o script SQL do projeto, caso exista, para criar as tabelas necessárias.

### 3. Configurar a ligação à base de dados

Atualiza as credenciais e os parâmetros de ligação na classe `DatabaseConnection.java`, de acordo com a configuração local do PostgreSQL.

Exemplo de URL JDBC:

```java
String url = "jdbc:postgresql://localhost:5432/sistema_bilhetes";
```

Não publiques palavras-passe ou outras credenciais privadas no repositório.

### 4. Adicionar o driver JDBC

Adiciona o driver PostgreSQL JDBC às dependências do projeto, através da configuração da IDE ou do sistema de construção utilizado.

### 5. Executar a aplicação

Abre o projeto no IntelliJ IDEA, localiza a classe principal que contém o método `main()` e executa-a.

## 📸 Capturas de Ecrã

As capturas de ecrã da aplicação podem ser adicionadas nesta secção para apresentar a interface gráfica.

Exemplo de organização:

```text
screenshots/
├── login.png
├── cliente.png
├── promotor.png
└── administrador.png
```

Quando as imagens estiverem no repositório, podes apresentá-las assim:

```markdown
![Ecrã de Login](screenshots/login.png)
```

## 🔐 Segurança

O sistema deve garantir que cada utilizador acede apenas às funcionalidades autorizadas para o seu perfil.

Entre as boas práticas a considerar estão:

- Validação dos dados introduzidos.
- Armazenamento seguro das palavras-passe através de hashing adequado.
- Utilização de consultas SQL parametrizadas.
- Separação das permissões de clientes, promotores e administradores.
- Proteção das credenciais da base de dados.

## 📚 Contexto Académico

Este projeto foi concebido no âmbito da unidade curricular de **Programação Orientada a Objetos**, com o propósito de consolidar conhecimentos de Java e desenvolver uma aplicação prática com interface gráfica e persistência de dados.

## 🔮 Melhorias Futuras

- Implementação de geração de bilhetes digitais com QR Code.
- Validação de bilhetes à entrada dos eventos.
- Emissão de comprovativos de compra.
- Relatórios de vendas e estatísticas.
- Melhorias na interface gráfica.
- Testes automatizados.
- Reforço dos mecanismos de autenticação e autorização.

## 🤝 Contribuição

Contribuições, sugestões e melhorias são bem-vindas.

1. Faz um fork do repositório.
2. Cria uma branch para a tua funcionalidade.
3. Regista as alterações através de commits claros.
4. Envia as alterações para o teu fork.
5. Abre um Pull Request para análise.

## 📄 Licença

Este projeto é desenvolvido para fins académicos. A licença de utilização e distribuição poderá ser definida posteriormente.

---

<p align="center">
  Desenvolvido com ☕ e dedicação à programação.
  <br/>
  <strong>Sistema de Gestão de Bilhetes de Eventos</strong>
</p>
