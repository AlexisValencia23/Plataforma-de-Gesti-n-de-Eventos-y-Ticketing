# Plataforma de Gestión de Eventos y Ticketing

## Integrantes
- Alexis Valencia

## Descripción
Plataforma distribuida basada en arquitectura de microservicios para la gestión integral de eventos y venta de tickets. Permite registrar usuarios, crear eventos, comprar tickets, procesar pagos, gestionar reservas, enviar notificaciones, aplicar descuentos y generar reportes.

El sistema está construido con Spring Boot y sigue el patrón CSR (Controller–Service–Repository), con comunicación REST entre microservicios mediante WebClient y un API Gateway centralizado para el enrutamiento de solicitudes. Todo el ecosistema está orquestado mediante Docker Compose con una base de datos PostgreSQL centralizada.

---

## Microservicios

| Microservicio | Puerto | Descripción |
|---|---|---|
| ms-usuarios | 8081 | Gestión de usuarios y autenticación |
| ms-eventos | 8082 | Gestión de eventos y disponibilidad |
| ms-tickets | 8083 | Compra y gestión de tickets |
| ms-pagos | 8084 | Procesamiento de pagos |
| ms-lugares | 8085 | Gestión de lugares y venues |
| ms-categorias | 8086 | Categorías de eventos |
| ms-reservas | 8087 | Reservas temporales de tickets |
| ms-notificaciones | 8088 | Envío de notificaciones |
| ms-reportes | 8089 | Generación de reportes |
| ms-descuentos | 8090 | Gestión de códigos de descuento |
| ms-gateway | 8091 | API Gateway - Enrutamiento centralizado |

---

## API Gateway

El API Gateway centraliza todas las solicitudes en el puerto **8091** y las redirige al microservicio correspondiente.

**Configuración:** `spring.cloud.gateway.server.webmvc.routes` (Spring Cloud Gateway MVC)

### Rutas principales del Gateway

| Ruta | Microservicio destino | Puerto |
|---|---|---|
| `http://localhost:8091/api/usuarios/**` | ms-usuarios | 8081 |
| `http://localhost:8091/api/eventos/**` | ms-eventos | 8082 |
| `http://localhost:8091/api/tickets/**` | ms-tickets | 8083 |
| `http://localhost:8091/api/pagos/**` | ms-pagos | 8084 |
| `http://localhost:8091/api/lugares/**` | ms-lugares | 8085 |
| `http://localhost:8091/api/categorias/**` | ms-categorias | 8086 |
| `http://localhost:8091/api/reservas/**` | ms-reservas | 8087 |
| `http://localhost:8091/api/notificaciones/**` | ms-notificaciones | 8088 |
| `http://localhost:8091/api/reportes/**` | ms-reportes | 8089 |
| `http://localhost:8091/api/descuentos/**` | ms-descuentos | 8090 |

### Ejemplos de uso del Gateway

```bash
# Listar todos los eventos
GET http://localhost:8091/api/eventos/todos

# Obtener un ticket específico
GET http://localhost:8091/api/tickets/1

# Listar pagos de un usuario
GET http://localhost:8091/api/pagos/usuario/1

# Listar reservas de un evento
GET http://localhost:8091/api/reservas/evento/1
```

---

## Documentación Swagger/OpenAPI

La documentación interactiva de los endpoints está disponible localmente una vez que el ecosistema esté en ejecución:

| Microservicio | URL Swagger |
|---|---|
| ms-eventos | http://localhost:8082/swagger-ui/index.html |
| ms-tickets | http://localhost:8083/swagger-ui/index.html |
| ms-pagos | http://localhost:8084/swagger-ui/index.html |
| ms-reservas | http://localhost:8087/swagger-ui/index.html |

---

## Tecnologías
- Java 17
- Spring Boot 4.0.6
- Spring Cloud Gateway MVC (ms-gateway)
- Spring Data JPA + Hibernate
- PostgreSQL 15 (orquestado en Docker)
- WebClient para comunicación entre microservicios
- springdoc-openapi (Swagger/OpenAPI 3.0)
- Bean Validation
- SLF4J para logs
- Lombok
- JUnit 5 + Mockito (pruebas unitarias)
- Docker & Docker Compose (orquestación completa del entorno)

---

## Pruebas Unitarias

Se implementaron pruebas unitarias con JUnit 5 y Mockito en los microservicios principales, siguiendo la estructura Given–When–Then:

| Microservicio | Tests | Cobertura |
|---|---|---|
| ms-eventos | 16 tests | EventoService completo |
| ms-tickets | 16 tests | TicketService completo |
| ms-pagos | 14 tests | PagoService completo |
| ms-reservas | 16 tests | ReservaService completo |

**Total: 62 pruebas unitarias**

Para ejecutar las pruebas de un microservicio, abrir el proyecto en IntelliJ IDEA y hacer clic derecho sobre la carpeta `src/test` → Run All Tests.

---

## Instrucciones de Ejecución con Docker Compose

### Requisitos previos
- Docker Desktop instalado y en ejecución

### Despliegue automatizado

El proyecto cuenta con una infraestructura completamente orquestada mediante contenedores. No es necesario instalar software de bases de datos externas ni ejecutar individualmente los microservicios desde el IDE.

1. Abre una terminal en la **carpeta raíz del proyecto**
2. Ejecuta el siguiente comando:

```bash
docker compose up
```

Docker Compose se encargará de:
- Levantar el contenedor de PostgreSQL 15
- Crear los esquemas de base de datos automáticamente
- Compilar y ejecutar los 11 microservicios

> **Nota:** Si realizaste cambios recientes en el código, usa `docker compose up --build` para forzar la recompilación completa.

### Variables de entorno configuradas

Cada microservicio se conecta a la base de datos mediante estas variables definidas en `docker-compose.yml`:

```
DB_URL=jdbc:postgresql://db-postgres:5432/eventos_db
DB_USER=postgres
DB_PASSWORD=root
```

---

## Instrucciones de Ejecución Local (sin Docker)

### Requisitos previos
- Java 17 instalado
- PostgreSQL instalado y corriendo en puerto 5432
- IntelliJ IDEA

### Variables de entorno requeridas

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=eventos_db
DB_USER=postgres
DB_PASSWORD=root
```

### Orden de ejecución
1. Iniciar PostgreSQL
2. Ejecutar los 10 microservicios (puertos 8081-8090)
3. Ejecutar ms-gateway (puerto 8091)

---

## Funcionalidades implementadas
- CRUD completo en todos los microservicios
- Patrón CSR (Controller–Service–Repository) en cada microservicio
- Comunicación entre microservicios via WebClient
- API Gateway centralizado con Spring Cloud Gateway MVC
- Validaciones con Bean Validation
- Manejo centralizado de excepciones con @ControllerAdvice
- Logs estructurados con SLF4J
- Persistencia real con JPA + Hibernate + PostgreSQL
- Respuestas REST con ResponseEntity y códigos HTTP correctos
- Documentación técnica con Swagger/OpenAPI 3.0
- Pruebas unitarias con JUnit 5 y Mockito (62 tests)
- Docker & Docker Compose para orquestación completa

---

## Gestión del Proyecto
- Tablero Trello: https://trello.com/invite/b/6a44a6d30d25f807e57a66db/ATTI710c958c0f8be0f38cb04ed10100d0da0463E5B3/plataforma-de-gestion-de-eventos-y-ticketing-dsy1103
