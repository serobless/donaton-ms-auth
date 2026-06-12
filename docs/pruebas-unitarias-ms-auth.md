# Pruebas Unitarias — ms-auth

## Resumen de ejecución (`mvn test` — 2026-06-11)

| Suite de tests | Tests | Fallos | Errores | Omitidos | Tiempo |
|---|---|---|---|---|---|
| `AuthServiceTest` | 4 | 0 | 0 | 0 | 0.462 s |
| `JwtServiceTest` | 3 | 0 | 0 | 0 | 0.179 s |
| `AuthControllerTest` | 5 | 0 | 0 | 0 | 4.570 s |
| **Total** | **12** | **0** | **0** | **0** | **~8.6 s** |

---

## AuthServiceTest

**Anotación:** `@ExtendWith(MockitoExtension.class)` — test unitario puro, sin contexto Spring.  
**Dependencias mockeadas:** `UsuarioRepository`, `JwtService`, `PasswordEncoder`, `AuthenticationManager`

| Test | Método testeado | Tipo | Resultado esperado | Tiempo |
|---|---|---|---|---|
| `loginExitoso` | `AuthService.login()` | Unitario | Retorna `AuthResponse` con token, email y rol correctos | 0.439 s |
| `loginFallido` | `AuthService.login()` | Unitario | Lanza `BadCredentialsException` cuando el `AuthenticationManager` rechaza las credenciales | 0.008 s |
| `registroExitoso` | `AuthService.register()` | Unitario | Retorna `AuthResponse` con token del nuevo usuario; verifica que `save()` y `encode()` se invocan | 0.005 s |
| `registroDuplicado` | `AuthService.register()` | Unitario | Lanza `DataIntegrityViolationException` cuando el repositorio falla por email duplicado | 0.003 s |

---

## JwtServiceTest

**Anotación:** ninguna (instancia directa con `ReflectionTestUtils`) — test unitario puro.  
**Configuración:** secret Base64 de 256 bits inyectado vía reflexión; expiración en 1 hora (o -1000 ms para el caso expirado).

| Test | Método(s) testeado(s) | Tipo | Resultado esperado | Tiempo |
|---|---|---|---|---|
| `generarTokenValido` | `generateToken()`, `extractUsername()`, `extractClaim()` | Unitario | Token no vacío; email extraído igual al del usuario; claim `rol` presente con valor `DONANTE` | 0.168 s |
| `validarTokenValido` | `isTokenValid()` | Unitario | Retorna `true` para un token recién generado con el mismo usuario | 0.004 s |
| `validarTokenExpirado` | `isTokenValid()` | Unitario | Lanza `ExpiredJwtException` cuando el token tiene `expiration = -1000 ms` | 0.006 s |

---

## AuthControllerTest

**Anotación:** `@WebMvcTest(AuthController.class)` + `@AutoConfigureMockMvc(addFilters = false)` — test de capa web.  
**Dependencias mockeadas:** `AuthService`, `JwtAuthenticationFilter`, `UsuarioDetailsService`  
**Nota técnica:** `addFilters = false` es necesario porque `OncePerRequestFilter.doFilter()` es `final` y no puede ser interceptado por Mockito; los filtros se deshabilitan para que las requests lleguen directamente al controller.

| Test | Endpoint testeado | Tipo | Resultado esperado | Tiempo |
|---|---|---|---|---|
| `testLogin_validCredentials_returns200WithToken` | `POST /auth/login` | Integración Web | HTTP 200 · body con `token` y `email` correctos | 0.330 s |
| `testLogin_wrongPassword_returns401` | `POST /auth/login` | Integración Web | HTTP 401 · `BadCredentialsException` capturada por `@ExceptionHandler` | 0.022 s |
| `testLogin_userNotFound_returns401` | `POST /auth/login` | Integración Web | HTTP 401 · `BadCredentialsException` capturada por `@ExceptionHandler` | 0.007 s |
| `testRegister_newUser_returns200` | `POST /auth/register` | Integración Web | HTTP 200 · body con `token` y `email` del nuevo usuario | 0.024 s |
| `testRegister_duplicateEmail_returns409` | `POST /auth/register` | Integración Web | HTTP 409 Conflict · `DataIntegrityViolationException` capturada por `@ExceptionHandler` | 0.008 s |

---

## Cobertura de casos por clase

| Clase bajo test | Casos cubiertos |
|---|---|
| `AuthService` | Login exitoso, login fallido, registro exitoso, registro con email duplicado |
| `JwtService` | Generación de token, validación de token válido, token expirado |
| `AuthController` | Login 200, login 401 (wrong password), login 401 (user not found), register 200, register 409 |
