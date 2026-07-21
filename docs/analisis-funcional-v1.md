# Sistema de Ventas en Ruta

## 1. Objetivo

Crear una aplicacion Android para registrar ventas en terreno, incluso sin conexion a internet, y sincronizar la informacion con un backend central cuando vuelva la conectividad.

## 2. Alcance de la primera version

La primera version permitira:

- Iniciar sesion.
- Crear, editar y buscar clientes.
- Crear y editar rutas, y asociar clientes a una ruta.
- Crear, editar y buscar productos por nombre o codigo.
- Consultar y ajustar stock.
- Registrar ventas con uno o varios productos.
- Asociar una venta a un cliente.
- Registrar forma y estado de pago.
- Consultar el historial por fecha, cliente, ruta, producto o codigo.
- Trabajar sin internet y sincronizar posteriormente.

Quedan fuera de la primera version:

- Facturacion electronica.
- Integracion con medios de pago.
- Gestion contable.
- Optimizacion automatica de rutas.

## 3. Usuarios

### Vendedor

- La primera version sera utilizada por un unico vendedor.
- El vendedor utilizara un telefono Android y un PC.
- Consulta clientes, rutas, productos y stock.
- Registra ventas, abonos y pagos desde Android.
- Trabaja con o sin conexion.

### Panel administrativo en PC

- No permite registrar ventas.
- Gestiona productos, stock, clientes y rutas.
- Consulta ventas, deudas, abonos e historial.
- Permite editar o anular ventas en cualquier momento, conservando el historial de cambios.
- Requiere conexion con el servidor; no tendra modo offline en la primera version.

### Administrador

- Gestiona productos, stock, clientes, rutas y usuarios.
- Consulta el historial completo.

En la primera version, las funciones administrativas pueden exponerse mediante la API y probarse con Postman antes de crear una interfaz dedicada.

## 4. Entidades iniciales

### Usuario

- `id`
- `nombre`
- `username`
- `password_hash`
- `rol`
- `activo`

### Ruta

- `id`
- `nombre`
- `activo`

### Cliente

- `id`
- `nombre`
- `telefono`
- `direccion`
- `ruta_id`
- `activo`

En la primera version, nombre, telefono, direccion y ruta son obligatorios. Los datos bancarios o de transferencia del cliente quedan fuera del alcance y se evaluaran posteriormente con requisitos especificos de seguridad.

### Producto

- `id`
- `codigo`
- `nombre`
- `unidad`
- `precio_costo`
- `precio_venta`
- `stock`
- `activo`

### Venta

- `id`
- `uuid`
- `fecha`
- `cliente_id`
- `usuario_id`
- `estado_pago`
- `estado_venta`
- `neto`
- `tasa_iva`
- `iva`
- `total`
- `estado_sincronizacion`
- `version`

### DetalleVenta

- `id`
- `venta_id`
- `producto_id`
- `cantidad`
- `precio_unitario`
- `subtotal`

`precio_unitario` conserva el precio aplicado al momento de la venta, aunque el precio actual del producto cambie.

### MovimientoPago

- `id`
- `uuid`
- `venta_id`
- `fecha`
- `tipo`
- `monto`
- `forma_pago`
- `estado_sincronizacion`
- `anulado`

Cada abono o devolucion se registra como un movimiento independiente. Esto permite pagar una deuda en varias partes, devolver dinero al anular y conservar la forma de pago de cada movimiento.

### MovimientoStock

- `id`
- `uuid`
- `producto_id`
- `fecha`
- `tipo`
- `cantidad`
- `venta_id`
- `observacion`

Los movimientos permiten reconstruir por que aumento o disminuyo el stock. Una venta genera una salida, una anulacion genera una devolucion y una edicion genera el ajuste correspondiente.

## 5. Reglas de negocio iniciales

1. El codigo de producto debe ser unico.
2. Una venta debe contener al menos un producto.
3. La cantidad vendida debe ser mayor que cero.
4. El total de la venta es la suma de sus subtotales.
5. Cada subtotal es `cantidad * precio_unitario`.
6. El stock disminuye al confirmar la venta, no al agregar un producto temporalmente.
7. Una venta confirmada puede editarse o anularse, pero nunca se elimina fisicamente y cada cambio debe quedar registrado.
8. Los registros con historial se desactivan en lugar de eliminarse fisicamente.
9. Con conexion, la aplicacion advertira y rechazara una venta con stock insuficiente.
10. Una venta creada sin conexion debe conservar un identificador local unico para evitar duplicados al sincronizar.
11. Sin conexion, se permitira confirmar una venta aunque el stock local sea insuficiente. Al sincronizar, el servidor registrara la salida y podra dejar el stock negativo para que la diferencia sea visible y corregible.
12. Las formas de pago admitidas son `EFECTIVO` y `TRANSFERENCIA`.
13. El estado de pago sera `PAGADO` cuando la suma de abonos validos cubra el total; en cualquier otro caso sera `PENDIENTE`.
14. El saldo pendiente se calcula como `total de la venta - suma de abonos validos`.
15. Un abono debe ser mayor que cero y no puede superar el saldo pendiente.
16. Los importes se expresaran en pesos chilenos y se almacenaran como numeros enteros, nunca con punto flotante.
17. Los precios incluyen IVA. Cada venta guardara su tasa, neto, IVA y total para conservar sus valores historicos.
18. La tasa de IVA sera configurable y no quedara escrita directamente en el codigo.
19. Al anular una venta, se devolvera al stock la cantidad vendida y la deuda asociada dejara de estar vigente.
20. Al editar productos o cantidades de una venta, se ajustara el stock por la diferencia y se recalcularan neto, IVA, total y saldo.
21. Al anular una venta con pagos, se registrara uno o mas movimientos de tipo `DEVOLUCION` por el dinero entregado al cliente.
22. Las operaciones realizadas desde el telefono y el PC se centralizaran en el servidor y utilizaran control de version para detectar cambios incompatibles.
23. Las ventas nuevas solo se registraran desde la aplicacion Android; el PC funcionara como panel administrativo.
24. Una venta confirmada podra editarse en cualquier momento. Cada edicion conservara trazabilidad y recalculara stock, neto, IVA, total y saldo pendiente.
25. Los productos se venden exclusivamente en unidades completas; stock y cantidades no admiten fracciones.
26. Todo cliente debe tener nombre, telefono, direccion y ruta.

## 6. Pantallas Android

### Login

- Usuario.
- Contrasena.
- Accion para ingresar.

### Inicio

- Nueva venta.
- Clientes.
- Productos.
- Historial.
- Estado de sincronizacion.

### Nueva venta

- Seleccion de cliente.
- Busqueda y seleccion de producto.
- Cantidad.
- Lista de productos agregados.
- Forma y estado de pago.
- Abono inicial opcional.
- Total.
- Confirmacion.

### Clientes

- Busqueda.
- Lista por ruta.
- Creacion y edicion segun permisos.

### Productos

- Busqueda por nombre o codigo.
- Precio y stock disponible.

### Historial

- Fecha desde y hasta.
- Cliente.
- Ruta.
- Producto o codigo.
- Detalle de una venta.

### Cuentas por cobrar

- Clientes con saldo pendiente.
- Ventas pendientes por cliente.
- Registro de abonos en efectivo o transferencia.
- Historial de abonos y saldo actualizado.

## 7. Arquitectura propuesta

```text
Aplicacion Android (Kotlin)
  |-- Room: datos locales y cola de sincronizacion
  |-- WorkManager: sincronizacion en segundo plano
  `-- Retrofit: comunicacion HTTPS
             |
             v
Backend REST (Spring Boot)
  |-- Spring Security
  |-- Servicios y reglas de negocio
  `-- Spring Data JPA
             |
             v
PostgreSQL
```

El PC accedera mediante una interfaz web conectada a la misma API. Servira para administrar productos, stock, clientes y rutas, y para consultar deudas e historial. No permitira registrar ventas y requerira conexion en la primera version.

El desarrollo comenzara localmente. La misma aplicacion se preparara para desplegar posteriormente el backend en Azure App Service y PostgreSQL en Azure Database for PostgreSQL.

## 8. Estrategia offline inicial

- Room sera la fuente de datos inmediata para la interfaz Android.
- Cada operacion pendiente tendra un identificador UUID y un estado de sincronizacion.
- Las ventas se guardaran primero de forma atomica en el dispositivo.
- Los abonos y cambios de venta tambien se guardaran primero en el dispositivo.
- WorkManager intentara enviar operaciones pendientes cuando exista conectividad.
- El servidor tratara cada UUID como una operacion idempotente para no duplicar ventas.
- La aplicacion mostrara si existen datos pendientes o errores de sincronizacion.
- No se utilizara la fecha del dispositivo como unico criterio para resolver conflictos.
- Aunque existe un solo vendedor, el telefono y el PC pueden modificar datos. El servidor validara la version recibida y rechazara una edicion si otro dispositivo actualizo antes el mismo registro.
- Al sincronizar una venta offline, el servidor aceptara el movimiento aunque produzca stock negativo y devolvera el stock actualizado al telefono.
- Las ediciones utilizaran un numero de version para detectar que se esta modificando el estado esperado de la venta.

Antes de implementar esta parte se documentaran los conflictos de stock, ediciones simultaneas, reintentos y anulaciones.

## 9. Azure por etapas

### Etapa 1: desarrollo local

- Spring Boot local.
- PostgreSQL local.
- Android con Room y emulador.
- Pruebas de API con Postman.

### Etapa 2: primera publicacion

- Backend en Azure App Service.
- Base de datos en Azure Database for PostgreSQL.
- Secretos fuera del repositorio.
- Comunicacion HTTPS.

### Etapa 3: operacion

- Azure Monitor y Application Insights.
- Azure Key Vault para secretos.
- Despliegue automatizado desde GitHub Actions.
- Blob Storage solo si se incorporan imagenes o comprobantes.

## 10. Tecnologias

- Editor principal: Visual Studio Code.
- Control de versiones: Git y GitHub.
- Backend: Java, Spring Boot, Maven y PostgreSQL.
- Android: Kotlin, XML, Material Design 3, Room, Retrofit, WorkManager y RecyclerView.
- Nube: Microsoft Azure en etapas posteriores.

Android Studio se utilizara cuando sea necesario administrar el SDK, el emulador o herramientas especificas de Android.

## 11. Decisiones pendientes

- Version minima de Android.
- Tecnologia de la interfaz web del PC.
- Tasa inicial de IVA y procedimiento para modificarla.

## 12. Criterios de exito de la primera version

- Una venta puede registrarse completamente sin internet.
- La venta se sincroniza una sola vez al recuperar conexion.
- El stock se actualiza de manera consistente.
- Una venta offline con stock insuficiente se sincroniza y deja visible cualquier stock negativo.
- Las ventas antiguas conservan el precio aplicado originalmente.
- Las ventas antiguas conservan su tasa y monto de IVA.
- Los abonos actualizan correctamente la deuda del cliente.
- Editar o anular una venta deja historial y ajusta el stock.
- Los filtros del historial entregan resultados correctos.
- Los errores de sincronizacion son visibles y se pueden reintentar.
