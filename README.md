# 🛒 FIAP CarStore - Vendas

O módulo **Vendas** faz parte do ecossistema **CarStore**, uma plataforma desenvolvida com foco em microsserviços.  
Ele é responsável por **gerenciar as transações de venda de veículos**, integrando informações dos módulos de **Clientes** e **Veículos**.

> ⚠️ **Atenção:** este projeto deve ser o **último** a ser iniciado, pois depende que os módulos **Clientes** e **Veículos** estejam ativos e acessíveis.

---

## 🧩 Arquitetura e Integração

O projeto segue os princípios da **Clean Architecture**, garantindo alta coesão, baixo acoplamento e fácil manutenção.  
Essa estrutura facilita a substituição de tecnologias, como bancos de dados ou provedores de autenticação, sem impacto nas regras de negócio.

**Principais benefícios aplicados:**
- Separação entre **regras de domínio**, **infraestrutura** e **interfaces** (controller, gateway, repository, etc.).
- Facilidade para **testes unitários e integração**.
- Independência de frameworks e serviços externos.

### 📦 Dependências de outros módulos

O módulo **Vendas** depende diretamente das APIs abaixo:

- 🔗 [FIAP CarStore - Veículos](https://github.com/fabriciofsousa/FIAP-carStore-veiculo)
- 🔗 [FIAP CarStore - Clientes](https://github.com/fabriciofsousa/FIAP-carStore-clientes)

Esses serviços devem estar em execução antes de iniciar o módulo de Vendas.

---

## ☁️ Branches e Ambientes

| Branch | Ambiente | Descrição |
|--------|-----------|------------|
| `master` | **AWS Cloud** | Código utilizado para execução e deploy automático na AWS. |
| `release/docker` | **Local (Docker)** | Versão configurada para rodar em ambiente local via Docker Compose. |

---

## 🚀 Executando Localmente (Branch `release/docker`)

1. **Certifique-se de que os módulos `clientes` e `veiculos` estão rodando** (via Docker ou endpoints acessíveis).

2. **Clone este repositório** e acesse a pasta do projeto:

```bash
git clone https://github.com/fabriciofsousa/FIAP-carStore-vendas.git
cd FIAP-carStore-vendas
git checkout release/docker
```

3. **Suba o ambiente com Docker Compose:**

```bash
docker compose up -d
```

Isso criará:
- Um container para a aplicação **vendas**.
- Uma instância do **MongoDB** para persistência dos dados.

4. **Verifique se a aplicação está no ar:**

```bash
http://localhost:8083/swagger-ui/index.html
```

---

## 🌐 Rede Compartilhada

O projeto utiliza a rede externa **`carstore-network`**, compartilhada entre os microsserviços.  
Isso garante a comunicação entre as APIs de **Clientes**, **Veículos** e **Vendas** sem necessidade de configuração manual de DNS.

---

## 📚 Documentação e Testes

### 🔹 Swagger UI
A documentação interativa da API pode ser acessada em:

> [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html)

---

## 🧪 Coleção do Postman

👉 **[Fiap-Carstore.postman_collection.json](src%2Fmain%2Fresources%2FFiap-Carstore.postman_collection.json)**
Variaveis:
[workspace.postman_globals.json](src%2Fmain%2Fresources%2Fworkspace.postman_globals.json)
---

## ⚙️ Comandos úteis (Branch `release/docker`)

Subir os serviços:
```bash
docker compose up -d
```

Ver logs em tempo real:
```bash
docker compose logs -f
```

Parar e remover containers:
```bash
docker compose down
```

Reconstruir as imagens:
```bash
docker compose build --no-cache
```

---

## 🧱 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **MongoDB**
- **Docker / Docker Compose**
- **AWS Cognito (ambiente cloud)**
- **Terraform (infraestrutura AWS)**
- **Clean Architecture**

---

## 📄 Licença

Projeto desenvolvido como parte da disciplina **Arquitetura e Desenvolvimento de Microsserviços** – FIAP.  
Todos os direitos reservados © 2025.
