# donaton-ms-auth

Microservicio de autenticación del proyecto Donaton (DuocUC DSY1106).
Gestiona registro, login y autorización de usuarios mediante JWT.

## Requisitos

- Java 17
- Maven 3.8+
- MySQL 8 corriendo en `localhost:3306`

## Cómo correrlo

```bash
# 1. Asegurate de tener MySQL levantado (XAMPP u otro)
# 2. La base de datos se crea automáticamente al levantar

mvn spring-boot:run
```

Levanta en el puerto **8083**.

## Base de datos

| Parámetro | Valor |
|---|---|
| Host | `localhost:3306` |
| Base de datos | `donaton_auth` (se crea sola) |
| Usuario | `root` |
| Contraseña | *(vacía)* |

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/register` | Público | Registrar nuevo usuario |
| POST | `/auth/login` | Público | Login, retorna JWT |
| GET | `/auth/me` | JWT requerido | Datos del usuario autenticado |
| GET | `/admin/usuarios` | ADMIN | Listar todos los usuarios |
| PATCH | `/admin/usuarios/{id}/rol` | ADMIN | Cambiar rol de un usuario |

## Roles disponibles

`ADMIN` · `DONANTE` · `EMPRESA` · `CENTRO_ADMIN`

## Registro de empresa

Para registrar una cuenta de empresa, incluir en el body:

```json
{
  "nombre": "Juan",
  "email": "juan@empresa.cl",
  "password": "123456",
  "rut": "12345678-9",
  "esEmpresa": true,
  "nombreEmpresa": "Mi Empresa SpA",
  "rutEmpresa": "76543210-K"
}
```
