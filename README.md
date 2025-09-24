# 🛒 FIAP CarStore - Vendas

O módulo **Vendas** é responsável pelo gerenciamento das transações
realizadas na plataforma CarStore.\
Ele depende dos outros serviços já estarem ativos, como **Veículo** e
**Clientes**, para garantir o correto funcionamento.

⚠️ **Atenção:** este projeto deve ser o **último** a ser iniciado, pois
depende que os demais estejam rodando antes.

------------------------------------------------------------------------

## 🚀 Para rodar localmente

1.  **Certifique-se de que os módulos `veiculo` e `clientes` já estão em
    execução.**

2.  Faça o **pull da imagem** do projeto `vendas`:

``` bash
docker pull fabriciofsousa/fiap-carstore-vendas:latest
```

3.  Suba o container do projeto `vendas`:

``` bash
docker compose up -d
```

Isso irá criar:
- Um container da aplicação **vendas**.
- Uma instância de **MongoDB** para persistência dos dados de vendas.

------------------------------------------------------------------------

## 🌐 Rede compartilhada

Assim como os outros módulos, este projeto utiliza a rede externa
**carstore-network**.\
Dessa forma, todos os serviços conseguem se comunicar e compartilhar
dados de forma integrada.

------------------------------------------------------------------------


## 🔗 Endpoints

- Swagger Vendas: [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html)

