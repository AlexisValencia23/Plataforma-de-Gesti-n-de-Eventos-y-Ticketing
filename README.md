# Plataforma de Gestión de Eventos y Ticketing

## Integrantes
- Alexis Valencia

## Descripción
Plataforma distribuida basada en arquitectura de microservicios para la gestión integral de eventos y venta de tickets. Permite registrar usuarios, crear eventos, comprar tickets, procesar pagos, gestionar reservas, enviar notificaciones, aplicar descuentos y generar reportes.

El sistema está construido con Spring Boot y sigue el patrón CSR (Controller–Service–Repository), con comunicación REST entre microservicios mediante WebClient y un API Gateway centralizado para el enrutamiento de solicitudes.

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
| `GET http://localhost:8091/api/usuarios/**` | ms-usuarios | 8081 |
| `GET http://localhost:8091/api/eventos/**` | ms-eventos | 8082 |
| `GET http://localhost:8091/api/tickets/**` | ms-tickets | 8083 |
| `GET http://localhost:8091/api/pagos/**` | ms-pagos | 8084 |
| `GET http://localhost:8091/api/lugares/**` | ms-lugares | 8085 |
| `GET http://localhost:8091/api/categorias/**` | ms-categorias | 8086 |
| `GET http://localhost:8091/api/reservas/**` | ms-reservas | 8087 |
| `GET http://localhost:8091/api/notificaciones/**` | ms-notificaciones | 8088 |
| `GET http://localhost:8091/api/reportes/**` | ms-reportes | 8089 |
| `GET http://localhost:8091/api/descuentos/**` | ms-descuentos | 8090 |

### Ejemplos de uso del Gateway

```bash
# Listar todos los eventos a través del Gateway
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

La documentación interactiva de los endpoints está disponible localmente una vez que cada microservicio esté en ejecución:

| Microservicio | URL Swagger |
|---|---|
| ms-eventos | http://localhost:8082/swagger-ui.html |
| ms-tickets | http://localhost:8083/swagger-ui.html |
| ms-pagos | http://localhost:8084/swagger-ui.html |
| ms-reservas | http://localhost:8087/swagger-ui.html |

---

## Tecnologías
- Java 26
- Spring Boot 4.0.6
- Spring Cloud Gateway MVC (ms-gateway)
- Spring Data JPA + Hibernate
- MySQL (Laragon)
- WebClient para comunicación entre microservicios
- springdoc-openapi (Swagger/OpenAPI 3.0)
- Bean Validation
- SLF4J para logs
- Lombok
- JUnit 5 + Mockito (pruebas unitarias)

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

## Instrucciones de Ejecución Local

### Requisitos previos
- Java 17 o superior instalado
- Laragon con MySQL corriendo
- IntelliJ IDEA

### Base de datos
Crear las siguientes bases de datos en MySQL:

```sql
CREATE DATABASE db_usuarios;
CREATE DATABASE db_eventos;
CREATE DATABASE db_tickets;
CREATE DATABASE db_pagos;
CREATE DATABASE db_lugares;
CREATE DATABASE db_categorias;
CREATE DATABASE db_reservas;
CREATE DATABASE db_notificaciones;
CREATE DATABASE db_reportes;
CREATE DATABASE db_descuentos;
```

### Orden de ejecución
Iniciar los microservicios en este orden desde IntelliJ IDEA:

1. Iniciar **Laragon** (MySQL debe estar corriendo)
2. Ejecutar **ms-usuarios** (puerto 8081)
3. Ejecutar **ms-eventos** (puerto 8082)
4. Ejecutar **ms-tickets** (puerto 8083)
5. Ejecutar **ms-pagos** (puerto 8084)
6. Ejecutar **ms-lugares** (puerto 8085)
7. Ejecutar **ms-categorias** (puerto 8086)
8. Ejecutar **ms-reservas** (puerto 8087)
9. Ejecutar **ms-notificaciones** (puerto 8088)
10. Ejecutar **ms-reportes** (puerto 8089)
11. Ejecutar **ms-descuentos** (puerto 8090)
12. Ejecutar **ms-gateway** (puerto 8091)

Una vez iniciados todos los servicios, se puede acceder a través del Gateway en `http://localhost:8091`.

---

## Funcionalidades implementadas
- CRUD completo en todos los microservicios
- Patrón CSR (Controller–Service–Repository) en cada microservicio
- Comunicación entre microservicios via WebClient
- API Gateway centralizado con Spring Cloud Gateway MVC
- Validaciones con Bean Validation
- Manejo centralizado de excepciones con @ControllerAdvice
- Logs estructurados con SLF4J
- Persistencia real con JPA + Hibernate
- Respuestas REST con ResponseEntity y códigos HTTP correctos
- Documentación técnica con Swagger/OpenAPI 3.0
- Pruebas unitarias con JUnit 5 y Mockito (62 tests)

## Gestión del Proyecto
- Tablero Trello: https://trello.com/invite/b/6a44a6d30d25f807e57a66db/ATTI710c958c0f8be0f38cb04ed10100d0da0463E5B3/plataforma-de-gestion-de-eventos-y-ticketing-dsy1103
