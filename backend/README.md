# Backend — Java + Spring Boot

API e regras de negócio do SystemDmove. Usa Java 17, Spring Boot, JPA/Hibernate e PostgreSQL.

## Estrutura (`src/main/java/com/systemdmove`)

- `controller/` — endpoints REST (recebem requisições e devolvem respostas)
- `service/` — regras de negócio e lógica principal
- `repository/` — acesso ao banco de dados (interfaces JPA)
- `model/` — entidades que representam as tabelas
- `dto/` — objetos de transferência de dados (entrada/saída da API)
- `config/` — configurações da aplicação

`src/main/resources/application.properties` — configuração do banco e do servidor.

## Como rodar

```bash
# A partir da pasta backend/
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

O backend sobe em `http://localhost:8080`. Teste: `GET http://localhost:8080/api/health`.

## Variáveis de ambiente

Copie `.env.example` para `.env` e ajuste a conexão com o PostgreSQL (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
