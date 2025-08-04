# Sistema Bancario TUP 2024

Un sistema bancario completo desarrollado con Spring Boot para manejar clientes, cuentas, operaciones y préstamos.

## ¿Qué hace?

Este sistema permite gestionar un banco básico con las siguientes funcionalidades:

### Clientes
- Registrar clientes nuevos (solo mayores de 18 años)
- Actualizar datos de clientes existentes
- Eliminar clientes (solo si no tienen plata en las cuentas)
- Consultar información de clientes

### Cuentas Bancarias
- Crear cuentas en pesos y dólares
- Tipos disponibles:
  - **Caja de Ahorro en Pesos** (CA$)
  - **Cuenta Corriente en Pesos** (CC$)  
  - **Caja de Ahorro en Dólares** (CAU$S)
- Ver saldos y historial de movimientos
- Un cliente puede tener máximo una cuenta por tipo y moneda

### Operaciones
- **Depósitos**: Ingresar dinero a una cuenta
- **Retiros**: Sacar dinero (si hay saldo suficiente)
- **Transferencias**: Mover plata entre cuentas de la misma moneda
- Todo queda registrado en el historial

### Préstamos
- Solicitar préstamos con evaluación automática
- El sistema evalúa tu "score crediticio" (simulado)
- Si te aprueban, la plata se deposita automáticamente
- Te dan un plan de cuotas fijas
- Solo podés pedir préstamos si tenés cuenta en esa moneda

## Tecnologías que usa

- **Spring Boot 3.3.0** - El framework principal
- **Java 16** - Lenguaje de programación
- **Maven** - Para manejar las dependencias
- **JUnit 5** - Para los tests
- **Mockito** - Para simular objetos en los tests

## Cómo ejecutarlo

### Lo que necesitás tener instalado
- Java 16 o más nuevo
- Maven 3.6+
- Algún IDE como IntelliJ IDEA o VS Code

### Pasos para ejecutar
1. Clonar el proyecto
```bash
git clone <url-del-repositorio>
cd tup2024
```

2. Compilar
```bash
mvn clean compile
```

3. Correr los tests
```bash
mvn test
```

4. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

Después podés acceder en `http://localhost:8080`

## Cómo usar la API

### Crear un cliente
```bash
POST http://localhost:8080/cliente
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "dni": 12345678,
  "fechaNacimiento": "1985-05-15",
  "tipoPersona": "F",
  "direccion": "Av. Siempre Viva 742",
  "telefono": "1234567890",
  "banco": "Banco Ejemplo"
}
```

### Crear una cuenta
```bash
POST http://localhost:8080/cuenta
Content-Type: application/json

{
  "tipoCuenta": "CAJA_AHORRO",
  "tipoMoneda": "PESOS",
  "dniTitular": 12345678,
  "saldoInicial": 10000.0
}
```

### Hacer un depósito
```bash
POST http://localhost:8080/operacion/deposito
Content-Type: application/json

{
  "numeroCuenta": 1234567890,
  "monto": 5000.0
}
```

### Solicitar un préstamo
```bash
POST http://localhost:8080/api/prestamo
Content-Type: application/json

{
  "numeroCliente": 12345678,
  "montoPrestamo": 100000.0,
  "plazoMeses": 12,
  "moneda": "PESOS"
}
```

## Todos los endpoints disponibles

### Clientes
- `POST /cliente` - Crear cliente
- `GET /cliente/{dni}` - Buscar por DNI
- `GET /cliente` - Listar todos
- `PUT /cliente/{dni}` - Actualizar
- `DELETE /cliente/{dni}` - Eliminar

### Cuentas
- `POST /cuenta` - Crear cuenta
- `GET /cuenta/{numeroCuenta}` - Consultar cuenta
- `GET /cuenta/cliente/{dni}` - Cuentas de un cliente
- `GET /cuenta/{numeroCuenta}/saldo` - Ver saldo
- `GET /cuenta/{numeroCuenta}/movimientos` - Ver historial

### Operaciones
- `POST /operacion/deposito` - Depositar
- `POST /operacion/retiro` - Retirar
- `POST /operacion/transferencia` - Transferir

### Préstamos
- `POST /api/prestamo` - Solicitar préstamo
- `GET /api/prestamo/{clienteId}` - Ver préstamos del cliente

## Validaciones importantes

### Para clientes
- El DNI tiene que ser único
- Tiene que ser mayor de 18 años
- Todos los campos son obligatorios
- El teléfono solo puede tener números, espacios, guiones y paréntesis

### Para cuentas
- Un cliente no puede tener dos cuentas iguales (mismo tipo y moneda)
- Solo se pueden crear los tipos de cuenta soportados
- El saldo inicial no puede ser negativo

### Para operaciones
- Los montos siempre tienen que ser positivos
- Para retirar tiene que haber saldo suficiente
- Las transferencias solo funcionan entre cuentas de la misma moneda
- Las cuentas tienen que existir

### Para préstamos
- El monto tiene que estar entre $1.000 y $1.000.000
- El plazo entre 3 y 60 meses
- El cliente tiene que tener una cuenta en la moneda que pide
- El sistema evalúa automáticamente si podés acceder al préstamo

## Cómo funciona el score crediticio

El sistema simula una consulta a un bureau de crédito:
- Genera un puntaje aleatorio del 1 al 10
- Si sacás 6 o más, te aprueban el préstamo
- Si sacás menos de 6, te lo rechazan
- Cada vez que consultás puede dar un resultado diferente

## Persistencia de datos

Los datos se guardan en memoria mientras la aplicación está corriendo. Cuando la reiniciás, se pierden todos los datos. Esto está pensado así para facilitar el testing y desarrollo.

## Tests

El proyecto tiene tests unitarios para todas las funcionalidades principales. Para ejecutarlos:

```bash
mvn test
```

Los tests cubren:
- Servicios de clientes, cuentas, operaciones y préstamos
- Validaciones de datos
- Manejo de errores
- Casos límite

## Estructura del proyecto

```
src/main/java/ar/edu/utn/frbb/tup/
├── controller/     # Los endpoints REST
├── service/        # La lógica de negocio
├── model/          # Las clases del dominio
├── persistence/    # Para guardar y leer datos
└── validator/      # Validaciones de entrada
```

## Notas para desarrolladores

- La aplicación usa una base de datos en memoria (simulada con Maps)
- Los números de cuenta se generan aleatoriamente
- Las cuotas de préstamos se calculan con el sistema francés
- Todos los movimientos quedan registrados con fecha y hora
- Las validaciones están centralizadas en clases Validator

```

Este proyecto fue desarrollado como trabajo práctico para la materia de Laboratorio de Computación 3 de la Tecnicatura Universitaria en programación en UTN FRBB.