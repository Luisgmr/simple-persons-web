# Prova Java PLENO SD - WEB - Configuração

Bem-vindo à prova prática para a vaga de programador full-stack em Java no SENAI Soluções Digitais. Ficamos felizes no seu interesse pela vaga, e desejamos uma ótima prova.  
Leia com atenção toda a documentação com os requisitos da prova que foi enviado a você e tente desenvolver o máximo que puder, mesmo que tenha que pular alguma etapa, desde que com qualidade e seguindo as regras de negócio.  
  
Lembrando que a configuração da prova fica a cargo do candidato, a realizar de acordo com os requisitos repassados ao candidato.  
  
Registrar nesse arquivo o que foi realizado da prova, as tecnologias utilizadas, o que não foi possível fazer e alguma observação que achar importante.

---

## 📋 Implementação Realizada

### ✅ Funcionalidades Implementadas

#### Backend (Spring Boot)
- **CRUD completo de Pessoa** com validações
- **Integração assíncrona** via RabbitMQ com API externa
- **Controle de situação de integração** (Não Enviado, Pendente, Sucesso, Erro)
- **Consulta de pessoas integradas** na API externa
- **Validação de CPF** e outros campos obrigatórios
- **Tratamento de mudança de CPF** (recria na API quando CPF é alterado)
- **Paginação** de resultados
- **Tratamento de erros** e mensagens personalizadas

#### API Externa (Spring Boot)
- **CRUD de Pessoa** com validações
- **Controle de datas** de criação e alteração
- **Validação de CPF único**
- **Endpoints RESTful** completos

#### Frontend (React)
- **Interface responsiva** com tema claro/escuro
- **Formulário de cadastro** com validações
- **Tabela paginada** de pessoas cadastradas
- **Busca de CEP** automática via ViaCEP
- **Integração manual** de pessoas pendentes
- **Consulta de pessoas integradas** na API
- **Feedback visual** de operações (toast notifications)
- **Componentes reutilizáveis** (DatePicker, Combobox, etc.)

#### Testes
- **Testes unitários** para services (Backend e API)
- **Testes de integração** para controllers
- **TestContainers** para testes com PostgreSQL e RabbitMQ
- **Teste de integração do fluxo completo** Backend -> Fila -> API
- **Gerador de CPF válido** para testes

### 🛠️ Tecnologias Utilizadas

#### Backend
- **Java 21** com Spring Boot 3.5
- **Spring Data JPA** para persistência
- **PostgreSQL** como banco de dados
- **RabbitMQ** para mensageria assíncrona
- **MapStruct** para mapeamento de DTOs
- **Flyway** para migração de banco
- **Docker** para containerização

#### Frontend
- **React 19** com Next.js
- **Tailwind CSS** para estilização
- **Shadcn/ui** para componentes
- **React Hook Form** para formulários
- **Zod** para validação
- **Framer Motion** para animações
- **Axios** para requisições HTTP

#### Testes
- **JUnit 5** para testes unitários
- **Mockito** para mocks
- **TestContainers** para testes de integração
- **AssertJ** para assertions

### 🚀 Como Executar

#### Pré-requisitos
- Docker e Docker Compose
- Java 21
- Node.js 18+

#### Execução Completa
```bash
# Subir toda a infraestrutura
docker-compose up --build -d

# Acessar aplicações
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
# API: http://localhost:8081
# RabbitMQ Management: http://localhost:15672
```

#### Execução para Desenvolvimento
```bash
# Subir apenas infraestrutura
docker-compose up postgres-web postgres-api rabbitmq -d

# Backend
cd backend
./mvnw spring-boot:run

# API
cd api
./mvnw spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

#### Executar Testes
```bash
# Preparar build local da API para testes (necessário para testar o fluxo completo)
cd api
docker build -t pessoa-api:local .

# Testes do Backend
cd backend
./mvnw test
# Ou se preferir, rodar pelo "Testar backend" do IntelliJ

# Testes da API
cd api
./mvnw test
```

### 🎯 Destaques da Implementação

1. **Arquitetura Assíncrona**: Integração via fila RabbitMQ para não bloquear operações
2. **Tratamento de Mudança de CPF**: Solução inteligente para recriar pessoa na API quando CPF é alterado
4. **Interface Moderna**: Design responsivo com tema claro/escuro
5. **Validações Robustas**: CPF, email, CEP e outros campos validados
6. **Testes Abrangentes**: Cobertura de testes unitários e de integração
7. **Containerização**: Aplicação totalmente dockerizada

### 📝 Observações

- Todos os requisitos funcionais foram implementados
- Aplicação segue padrões de Clean Code e SOLID
- Interface intuitiva e responsiva
- Tratamento adequado de erros e edge cases
- Documentação clara e código bem estruturado

Por *Luisgmr* — [LinkedIn](https://linkedin.com/in/Luisgmr) | [GitHub](https://github.com/Luisgmr)
