Plataforma de Gestión de Eventos y Ticketing

Integrantes
Alexis Valencia

Descripción
Plataforma distribuida basada en arquitectura de microservicios para la gestión integral de eventos y venta de tickets. Permite registrar usuarios, crear eventos, comprar tickets, procesar pagos, gestionar reservas, enviar notificaciones, aplicar descuentos y generar reportes.

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

## Tecnologías
- Java 26
- Spring Boot 4.0.6
- Spring Data JPA + Hibernate
- MySQL (Laragon)
- WebClient para comunicación entre microservicios
- Bean Validation
- SLF4J para logs
- Lombok

## Pasos para ejecutar

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

### Ejecución
Iniciar los microservicios en este orden:
1. ms-usuarios (puerto 8081)
2. ms-eventos (puerto 8082)
3. ms-tickets (puerto 8083)
4. ms-pagos (puerto 8084)
5. ms-lugares (puerto 8085)
6. ms-categorias (puerto 8086)
7. ms-reservas (puerto 8087)
8. ms-notificaciones (puerto 8088)
9. ms-reportes (puerto 8089)
10. ms-descuentos (puerto 8090)

## Funcionalidades implementadas
- CRUD completo en todos los microservicios
- Comunicación entre microservicios via WebClient
- Validaciones con Bean Validation
- Manejo centralizado de excepciones con @ControllerAdvice
- Logs estructurados con SLF4J
- Persistencia real con JPA + Hibernate
- Respuestas REST con ResponseEntity y códigos HTTP correctos
