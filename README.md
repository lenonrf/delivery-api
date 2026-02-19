# DELIVERY API - DASHER PAYOUT CALCULATION

API REST para calcular pagamentos de entregadores (dashers) baseado em entregas realizadas. Projeto desenvolvido como prática para entrevistas técnicas no estilo DoorDash/Uber Eats.

---

## VISÃO GERAL

Sistema que calcula o valor a ser pago aos entregadores considerando:

- Número de entregas realizadas
- Tempo total gasto nas entregas
- Distância percorrida
- Horários de pico (peak hours)

---

## TECNOLOGIAS

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok**
- **Maven**
- **JUnit 5 + Mockito**

---

## ESTRUTURA DO PROJETO

```
delivery-api/
├── src/
│   ├── main/
│   │   ├── java/com/lab13/delivery_api/
│   │   │   ├── controller/
│   │   │   │   └── PayoutController.java
│   │   │   ├── service/
│   │   │   │   ├── PayoutService.java
│   │   │   │   ├── DeliveryService.java
│   │   │   │   └── MockDeliveryService.java
│   │   │   ├── model/
│   │   │   │   ├── Delivery.java
│   │   │   │   └── Product.java
│   │   │   ├── dto/
│   │   │   │   ├── PayoutRequest.java
│   │   │   │   └── PayoutResponse.java
│   │   │   └── DeliveryApiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/lab13/delivery_api/
│           └── service/
│               └── PayoutServiceTest.java
├── pom.xml
└── README.md
```

---

## REGRAS DE PAGAMENTO

### Base Pay (Pagamento Base)

- **$10.00 por entrega**

### Time Pay (Pagamento por Tempo)

- **$0.50 por minuto** trabalhado
- **Horário de Pico (18h - 21h):** multiplicador de **1.5x**

### Distance Pay (Pagamento por Distância)

- **$1.00 por quilômetro** percorrido

### Fórmula Final

```
Total Payout = Base Pay + Time Pay + Distance Pay

Onde:
- Base Pay = $10.00 × número de entregas
- Time Pay = ($0.50 × minutos normais) + ($0.50 × minutos pico × 1.5)
- Distance Pay = $1.00 × distância total (km)
```

---

## ENDPOINTS

### Calcular Pagamento

**POST** `/api/dasher/payout`

**Request Body:**

```json
{
  "dasherId": "dasher-123",
  "date": "2024-02-14"
}
```

**Response:**

```json
{
  "dasherId": "dasher-123",
  "date": "2024-02-14",
  "totalDeliveries": 3,
  "totalMinutes": 105,
  "totalDistance": 17.2,
  "basePay": 30.00,
  "timePay": 60.00,
  "peakTimePay": 22.50,
  "distancePay": 17.20,
  "totalPayout": 107.20
}
```

---

## EXEMPLO DE CÁLCULO

### Cenário: Dasher com 3 entregas

**Entrega 1:** 10:00 - 10:30 | 5.5 km (fora do pico)
**Entrega 2:** 14:00 - 14:45 | 8.2 km (fora do pico)
**Entrega 3:** 19:00 - 19:30 | 3.5 km (horário de pico)

### Cálculos:

**Base Pay:**

- 3 entregas × $10.00 = **$30.00**

**Time Pay:**

- Minutos normais: 30min + 45min = 75min
- Minutos pico: 30min
- Normal: 75min × $0.50 = $37.50
- Pico: 30min × $0.50 × 1.5 = $22.50
- Total Time Pay: **$60.00**

**Distance Pay:**

- (5.5 + 8.2 + 3.5) km × $1.00 = **$17.20**

**Total Payout:**

- $30.00 + $60.00 + $17.20 = **$107.20**

---

## COMO EXECUTAR

### Pré-requisitos

- Java 17+
- Maven 3.6+

### Compilar

```bash
mvn clean compile
```

### Executar

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### Executar Testes

```bash
mvn test
```

---

## TESTE COM cURL

```bash
curl -X POST http://localhost:8080/api/dasher/payout \
  -H "Content-Type: application/json" \
  -d '{
    "dasherId": "dasher-123",
    "date": "2024-02-14"
  }'
```

---

## DADOS MOCK

O `MockDeliveryService` fornece 3 entregas de exemplo:

- **d1:** 10:00-10:30 | 5.5 km (normal)
- **d2:** 14:00-14:45 | 8.2 km (normal)
- **d3:** 19:00-19:30 | 3.5 km (pico)

---

## TESTES IMPLEMENTADOS

- Cálculo correto sem horário de pico
- Cálculo correto com horário de pico
- Validação de multiplicador de horário de pico
- Cálculo de distância total
- Cálculo de tempo total
- Identificação correta de peak hours (18h-21h)

---

## PRÓXIMAS MELHORIAS

- Adicionar validação de entrada com Bean Validation
- Implementar exception handling global
- Adicionar persistência real (substituir mock)
- Criar endpoints para CRUD de entregas
- Adicionar suporte a gorjetas (tips)
- Implementar relatórios semanais/mensais
- Adicionar autenticação e autorização
- Documentação com Swagger/OpenAPI
