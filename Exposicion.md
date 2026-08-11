# Exposición Técnica — Sistema de Facturación

---

## 1. Ubicación de la Base de Datos

El proyecto usa **SQLite** como motor de base de datos. El archivo se llama:

```
sistemafacturacion.db
```

La clase `DatabaseConnection` busca el archivo en el siguiente orden de prioridad:

| Prioridad | Ruta buscada                                         |
|-----------|------------------------------------------------------|
| 1         | Directorio de trabajo actual (`.`)                   |
| 2         | Subcarpeta `sistemafacturacion/`                     |
| 3         | `./target/sistemafacturacion/`                       |
| 4         | `{user.dir}/sistemafacturacion.db`                   |
| 5         | `{user.dir}/sistemafacturacion/sistemafacturacion.db`|

> En un servidor Tomcat, `user.dir` suele ser el directorio raíz de Tomcat (`/opt/tomcat` o `C:\tomcat`).  
> En desarrollo con IntelliJ, el archivo se crea en la raíz del módulo: `sistemafacturacion/sistemafacturacion.db`.

### Tablas en la Base de Datos

| Tabla                  | Descripción                                          |
|------------------------|------------------------------------------------------|
| `Roles`                | Roles del sistema (admin=1, usuario=2)               |
| `Usuarios`             | Usuarios con credenciales y rol asignado             |
| `Clientes`             | Clientes registrados con RTN                         |
| `Productos`            | Catálogo de productos con precio y stock             |
| `CAI`                  | Códigos de Autorización de Impresión                 |
| `Facturas`             | Cabecera de facturas (totales, fechas, cliente)      |
| `DetalleFacturas`      | Líneas de cada factura (producto, cantidad, precio)  |
| `HistorialTransacciones` | Registro de operaciones realizadas                  |

---

## 2. Estructura de Carpetas del Proyecto

```
sistemafacturacion/
├── src/
│   └── main/
│       ├── java/com/example/sistemafacturacion/
│       │   ├── beans/          ← CONTROLADOR (MVC)
│       │   ├── converters/     ← Conversores JSF
│       │   ├── data/           ← MODELO (MVC)
│       │   ├── database/       ← Acceso a datos (DAO)
│       │   ├── interfaces/     ← Contratos / Abstracciones
│       │   │   ├── interactor/ ← Interfaces de lógica de negocio
│       │   │   ├── repository/ ← Interfaces de acceso a datos
│       │   │   └── viewmodel/  ← Interfaces de los beans
│       │   └── services/       ← Lógica de negocio
│       └── webapp/             ← VISTA (MVC)
│           ├── WEB-INF/        ← Configuración del servidor
│           ├── resources/css/  ← Estilos
│           ├── *.xhtml         ← Páginas de la aplicación
└── pom.xml                     ← Dependencias Maven
```

### Detalle de cada carpeta Java

#### `beans/` — Controlador
Contiene los **Managed Beans** de JSF. Son el punto de contacto entre la vista (XHTML) y la lógica de negocio. Cada bean es `@Named` y `@ViewScoped` (persiste mientras la vista esté activa).

| Clase           | Responsabilidad                                                   |
|-----------------|-------------------------------------------------------------------|
| `LoginBean`     | Autenticación de usuarios y manejo de sesión                      |
| `SessionBean`   | Provee el usuario actual y verifica si es administrador           |
| `FacturaBean`   | Crear/guardar facturas, agregar líneas, ver detalles, generar PDF |
| `CAIBean`       | CRUD de CAI con validación de rol admin                           |
| `ClienteBean`   | CRUD de clientes                                                  |
| `ProductoBean`  | CRUD de productos                                                 |
| `UsuarioBean`   | CRUD de usuarios con validación de rol admin                      |

#### `data/` — Modelo
Clases **POJO** (Plain Old Java Objects). Representan las entidades del negocio y se mapean directamente con las tablas de la base de datos.

| Clase                  | Tabla DB               |
|------------------------|------------------------|
| `Usuario`              | `Usuarios`             |
| `Rol`                  | `Roles`                |
| `Cliente`              | `Clientes`             |
| `Producto`             | `Productos`            |
| `CAI`                  | `CAI`                  |
| `Factura`              | `Facturas`             |
| `DetalleFactura`       | `DetalleFacturas`      |
| `HistorialTransacciones` | `HistorialTransacciones` |

#### `database/` — Acceso a Datos (DAO)
Implementaciones concretas de los repositorios. Contienen las consultas SQL usando JDBC.

| Clase                        | Responsabilidad                                         |
|------------------------------|---------------------------------------------------------|
| `DatabaseConnection`         | Singleton de conexión SQLite + inicialización de tablas |
| `FacturaRepositoryImpl`      | CRUD de facturas con JOIN a clientes                    |
| `DetalleFacturaRepositoryImpl` | CRUD de líneas con JOIN a productos                  |
| `ClienteRepositoryImpl`      | CRUD de clientes                                        |
| `ProductoRepositoryImpl`     | CRUD de productos + control de stock                    |
| `CAIRepositoryImpl`          | CRUD de CAI + obtener CAI activo                        |
| `UsuarioRepositoryImpl`      | CRUD de usuarios + autenticación                        |

#### `interfaces/` — Contratos
Define interfaces que permiten desacoplar capas:
- **`interactor/`**: Qué puede hacer el servicio (lógica de negocio).
- **`repository/`**: Qué puede hacer el repositorio (acceso a datos).
- **`viewmodel/`**: Qué expone el bean a la vista.

#### `services/` — Lógica de Negocio
Implementan las interfaces `interactor/`. Orquestan operaciones complejas como la creación de factura en una sola transacción JDBC.

| Clase                 | Responsabilidad                                                |
|-----------------------|----------------------------------------------------------------|
| `FacturaService`      | Crea factura + detalles en transacción + genera PDF (OpenPDF)  |
| `CAIService`          | Valida rangos, obtiene siguiente número de factura             |
| `ClienteService`      | Validaciones de negocio al registrar clientes                  |
| `ProductoService`     | Verificación de stock disponible                               |
| `UsuarioService`      | Autenticación y registro de usuarios                           |
| `DetalleFacturaService` | Consulta de detalles por factura                             |

#### `converters/`
Permiten que JSF convierta entre el objeto Java y su representación en la vista (necesario para `p:autoComplete`).

| Clase               | Propósito                                          |
|---------------------|----------------------------------------------------|
| `ClienteConverter`  | Convierte `Cliente` ↔ String (para autocomplete)   |
| `ProductoConverter` | Convierte `Producto` ↔ String (para autocomplete)  |

#### `webapp/` — Vista
Páginas XHTML que usan **JSF + PrimeFaces**.

| Archivo                  | Descripción                                          |
|--------------------------|------------------------------------------------------|
| `login.xhtml`            | Formulario de inicio de sesión                       |
| `inicio.xhtml`           | Dashboard con tarjetas de navegación por módulo      |
| `facturas.xhtml`         | Creación de facturas y lista de registradas          |
| `historial_facturas.xhtml` | Historial completo con detalle y resumen financiero|
| `clientes.xhtml`         | CRUD de clientes                                     |
| `productos.xhtml`        | CRUD de productos                                    |
| `cai.xhtml`              | CRUD de CAI (solo admin)                             |
| `usuarios.xhtml`         | CRUD de usuarios (solo admin)                        |
| `WEB-INF/web.xml`        | Configuración del servlet JSF y tema PrimeFaces      |
| `WEB-INF/beans.xml`      | Activa CDI (Weld)                                    |
| `WEB-INF/faces-config.xml` | Configuración de JSF (vacía, se usa CDI)           |

---

## 3. Uso de AJAX: `update` y `process`

PrimeFaces usa AJAX para actualizar partes de la página sin recargarla completamente.

### `process` — Qué se envía al servidor

Define qué componentes del formulario se procesan (validan y envían sus valores) en la petición AJAX.

| Valor       | Significado                                              |
|-------------|----------------------------------------------------------|
| `@form`     | Procesa todos los campos del formulario actual           |
| `@this`     | Solo procesa el componente que dispara el evento         |

**Ejemplos en el proyecto:**

```xml
<!-- facturas.xhtml: al guardar, procesa todo el formulario -->
<p:commandButton action="#{facturaBean.guardarFactura}" process="@form" update="@form"/>

<!-- cai.xhtml: al eliminar solo procesa el botón mismo -->
<p:commandButton action="#{caiBean.eliminarCAI(cai)}" process="@this" update="messages dataTableCAI"/>

<!-- facturas.xhtml: botón limpiar, no valida nada del form -->
<p:commandButton action="#{caiBean.limpiarFormulario}" process="@this" immediate="true"/>
```

---

### `update` — Qué se refresca en el navegador

Define qué componentes de la página se vuelven a renderizar con la respuesta del servidor.

| Valor                         | Significado                                           |
|-------------------------------|-------------------------------------------------------|
| `@form`                       | Actualiza todo el formulario                          |
| `messages`                    | Actualiza solo el panel de mensajes                   |
| `subtotal total`              | Actualiza dos componentes específicos por ID          |
| `detalleTable subtotal total` | Actualiza tres componentes                            |
| `:formFactura:detalleDialog`  | Ruta absoluta (necesaria dentro de DataTable)         |

**Ejemplos en el proyecto:**

```xml
<!-- facturas.xhtml: al seleccionar producto, refresca el campo precio -->
<p:ajax event="itemSelect"
        listener="#{facturaBean.onProductoSelect}"
        update="precioUnitario"/>

<!-- facturas.xhtml: al cambiar descuento, recalcula totales -->
<p:ajax event="change"
        listener="#{facturaBean.onValoresFinancierosChange}"
        update="subtotal total"/>

<!-- facturas.xhtml: al agregar producto a la línea -->
<p:commandButton action="#{facturaBean.agregarLinea}"
                 update="detalleTable subtotal total messages"/>

<!-- facturas.xhtml: botón "Ver detalle" dentro de DataTable scrollable -->
<!-- Se usa ruta ABSOLUTA porque scrollable=true saca el tbody del árbol JSF -->
<p:commandButton action="#{facturaBean.obtenerDetalles}"
                 update=":formFactura:detalleDialog"
                 oncomplete="PF('detalleDialogWidget').show()"/>

<!-- historial: mismo patrón con ruta absoluta -->
<p:commandButton action="#{facturaBean.obtenerDetalles}"
                 update=":formHistorial:dialogDetalle"
                 oncomplete="PF('dlgDetalle').show()"/>
```

> **¿Por qué ruta absoluta en los diálogos?**  
> Cuando un `p:dataTable` tiene `scrollable="true"`, PrimeFaces renderiza el `<tbody>` fuera
> del contenedor JSF normal. Al buscar `"detalleDialog"` de forma relativa, JSF no lo encuentra
> porque el botón está en un naming container diferente. La ruta absoluta `:formId:componentId`
> fuerza la búsqueda desde la raíz del árbol de componentes.

---

## 4. Patrón MVC con Capas para la Conexión a la Base de Datos

El proyecto implementa un patrón **MVC con capas adicionales** de servicio y repositorio, lo que lo acerca a una arquitectura limpia.

```
┌─────────────────────────────────────────────────────┐
│                    VISTA (View)                      │
│   *.xhtml — PrimeFaces + JSF EL expressions         │
│   Ej: #{facturaBean.guardarFactura}                  │
└──────────────────────┬──────────────────────────────┘
                       │ Llama métodos del bean
┌──────────────────────▼──────────────────────────────┐
│                 CONTROLADOR (Controller)             │
│   beans/*.java — @Named @ViewScoped                 │
│   Ej: FacturaBean, CAIBean, UsuarioBean              │
│   - Recibe eventos de la vista                       │
│   - Llama al servicio correspondiente                │
│   - Actualiza el modelo para la vista                │
└──────────────────────┬──────────────────────────────┘
                       │ Delega lógica de negocio
┌──────────────────────▼──────────────────────────────┐
│                  SERVICIO (Service)                  │
│   services/*.java — implementa interfaces interactor │
│   Ej: FacturaService, CAIService                     │
│   - Validaciones de negocio                          │
│   - Orquesta transacciones JDBC                      │
│   - Genera PDF                                       │
└──────────────────────┬──────────────────────────────┘
                       │ Delega acceso a datos
┌──────────────────────▼──────────────────────────────┐
│               REPOSITORIO (Repository)               │
│   database/*RepositoryImpl.java                      │
│   Ej: FacturaRepositoryImpl, ClienteRepositoryImpl   │
│   - Ejecuta SQL con PreparedStatement                │
│   - Mapea ResultSet → objetos del modelo             │
│   - No contiene lógica de negocio                    │
└──────────────────────┬──────────────────────────────┘
                       │ Obtiene conexión
┌──────────────────────▼──────────────────────────────┐
│             CONEXIÓN (DatabaseConnection)            │
│   database/DatabaseConnection.java — Singleton       │
│   - Carga el driver SQLite                           │
│   - Construye la URL de conexión                     │
│   - Crea tablas si no existen (CREATE TABLE IF NOT)  │
│   - Hace seed de roles y usuario admin               │
│   - Provee Connection via DriverManager              │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│              BASE DE DATOS (SQLite)                  │
│   sistemafacturacion.db                              │
│   8 tablas: Roles, Usuarios, Clientes, Productos,    │
│             CAI, Facturas, DetalleFacturas,           │
│             HistorialTransacciones                   │
└─────────────────────────────────────────────────────┘
```

### Ejemplo: flujo de "Guardar Factura"

```
1. [Vista] facturas.xhtml
   → <p:commandButton action="#{facturaBean.guardarFactura}"/>

2. [Controlador] FacturaBean.guardarFactura()
   → Valida que haya líneas, CAI activo, rango válido
   → Construye objeto Factura
   → Llama: facturaInteractor.crearFactura(factura, detalles)

3. [Servicio] FacturaService.crearFactura()
   → Obtiene conexión de DatabaseConnection.getInstance()
   → conn.setAutoCommit(false)  ← inicia transacción
   → facturaRepo.crearConConexion(conn, factura)
   → Para cada detalle: detalleRepo.crearConConexion(conn, detalle)
   → productoRepo.actualizarStockConConexion(conn, idProducto, cantidad)
   → caiRepo.actualizarSiguienteFactura(conn, idCAI, siguiente)
   → conn.commit()  ← confirma transacción

4. [Repositorio] FacturaRepositoryImpl.crearConConexion()
   → PreparedStatement: INSERT INTO Facturas (...)
   → Retorna el ID generado (RETURN_GENERATED_KEYS)

5. [BD] SQLite ejecuta la operación y persiste los datos
```

### Por qué se usan interfaces (`interactor` y `repository`)

```
FacturaBean  →  FacturaInteractor (interfaz)  ←  FacturaService (implementación)
                        ↓
              FacturaRepository (interfaz)  ←  FacturaRepositoryImpl (implementación)
```

Las interfaces permiten:
- **Desacoplamiento**: el bean no sabe si la implementación usa SQLite, MySQL u otro motor.
- **Testabilidad**: se pueden crear implementaciones mock para pruebas sin tocar la BD.
- **Mantenibilidad**: cambiar el motor de BD solo requiere una nueva implementación del repositorio.

---

## 5. Control de Acceso por Roles

```
Login exitoso
     │
     ▼
session.setAttribute("usuarioAutenticado", usuario)
     │
     ▼
SessionBean.isAdmin()
     │
     ├── idRol == 1 → Admin
     │     ├── Ve tarjetas CAI y Usuarios en inicio.xhtml
     │     ├── Puede acceder y operar en cai.xhtml
     │     └── Puede acceder y operar en usuarios.xhtml
     │
     └── idRol == 2 → Usuario estándar
           ├── NO ve tarjetas CAI ni Usuarios (rendered=false)
           ├── Si entra a cai.xhtml → ve panel "Acceso Denegado"
           └── Si entra a usuarios.xhtml → ve panel "Acceso Denegado"
```

La verificación es **doble**:
1. **Vista** (`rendered`): oculta los componentes al renderizar el HTML.
2. **Bean** (`@PostConstruct` + guard en métodos): redirige/bloquea aun si el usuario
   manipula la URL directamente.
