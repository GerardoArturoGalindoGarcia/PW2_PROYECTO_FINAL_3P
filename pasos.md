# Sistema de Facturación con CAI/RTN - Documentación de Pasos Realizados

## Resumen Ejecutivo
Se ha creado un **Sistema de Facturación Electrónica con CAI/RTN** utilizando Java 25, JSF con Primefaces 15.0.16, y SQLite como base de datos. El sistema sigue la arquitectura MVC con Managed Beans y utiliza el patrón Singleton para la conexión a la base de datos.

---

## 1. ESTRUCTURA DEL PROYECTO

### 1.1 Carpetas Creadas
Se creó la siguiente estructura de carpetas siguiendo los estándares especificados:

```
src/main/java/com/example/sistemafacturacion/
├── data/                          # Clases de datos (POJOs)
├── database/                      # Capa de acceso a datos
├── beans/                         # Managed Beans (Controladores JSF)
├── services/                      # Lógica de negocio (Interactors)
└── interfaces/
    ├── repository/               # Interfaces de acceso a datos
    ├── interactor/              # Interfaces de casos de uso
    └── viewmodel/               # Interfaces de vista
```

### 1.2 Estructura de Webapp
```
src/main/webapp/
├── WEB-INF/
│   ├── web.xml                   # Configuración de la aplicación
│   └── faces-config.xml
├── index.xhtml                   # Página de inicio (redirección)
├── login.xhtml                   # Pantalla de autenticación
├── inicio.xhtml                  # Dashboard principal
├── usuarios.xhtml                # Gestión de usuarios
├── clientes.xhtml                # Gestión de clientes
├── productos.xhtml               # Gestión de productos
├── facturas.xhtml                # Gestión de facturas
└── cai.xhtml                     # Gestión de CAI/RTN
```

---

## 2. CLASES DE DATOS (POJOs)

Se crearon las siguientes clases de datos en `src/main/java/com/example/sistemafacturacion/data/`:

### 2.1 Usuario.java
- Representa a los usuarios del sistema
- Campos: idUsuario, nombreUsuario, contrasena, nombre, apellido, email, idRol, estado
- Relacionada con la tabla Usuarios

### 2.2 Rol.java
- Define los roles de usuario en el sistema
- Campos: idRol, nombre, descripcion
- Relacionada con la tabla Roles

### 2.3 Cliente.java
- Almacena información de los clientes
- Campos: idCliente, nombre, rtn, email, telefono, direccion, ciudad, estado
- Relacionada con la tabla Clientes

### 2.4 Producto.java
- Gestiona el catálogo de productos
- Campos: idProducto, nombre, descripcion, precioVenta, stock, estado
- Relacionada con la tabla Productos

### 2.5 CAI.java
- Administra los CAI (Código de Autorización de Impresión) y rangos de facturación
- Campos: idCAI, cai, rtn, rangoInicial, rangoFinal, siguienteFactura, fechaEmision, fechaVencimiento, estado
- Relacionada con la tabla CAI

### 2.6 Factura.java
- Representa las facturas electrónicas
- Campos: idFactura, numeroFactura, cai, idCliente, fechaFactura, subtotal, descuento, impuesto, total, estado
- Relacionada con la tabla Facturas

### 2.7 DetalleFactura.java
- Detalles de los productos en cada factura
- Campos: idDetalle, idFactura, idProducto, cantidad, precioUnitario, subtotal
- Relacionada con la tabla DetalleFacturas

### 2.8 HistorialTransacciones.java
- Registro de todas las transacciones del sistema
- Campos: idTransaccion, idFactura, idUsuario, fechaTransaccion, tipoTransaccion, descripcion
- Relacionada con la tabla HistorialTransacciones

---

## 3. INTERFACES MVC

Se implementó el patrón MVC con interfaces claramente definidas:

### 3.1 Interfaces Repository (Acceso a Datos)
Ubicadas en `interfaces/repository/`:
- **UsuarioRepository**: Operaciones CRUD para usuarios
- **ClienteRepository**: Operaciones CRUD para clientes con búsqueda
- **ProductoRepository**: Operaciones CRUD para productos con actualización de stock
- **FacturaRepository**: Operaciones de facturas con filtros por cliente, fecha y estado
- **CAIRepository**: Operaciones de CAI con búsqueda de CAI activo

### 3.2 Interfaces Interactor (Casos de Uso)
Ubicadas en `interfaces/interactor/`:
- **UsuarioInteractor**: Autenticación, gestión de usuarios y cambio de contraseña
- **ClienteInteractor**: Registro y gestión de clientes
- **ProductoInteractor**: Gestión de productos y verificación de stock
- **FacturaInteractor**: Creación de facturas, generación de PDF y cálculo de totales
- **CAIInteractor**: Gestión de CAI y validación de rangos de facturación

### 3.3 Interfaces ViewModel (Vistas)
Ubicadas en `interfaces/viewmodel/`:
- **UsuarioViewModel**: Interfaz para el bean de usuarios
- **ClienteViewModel**: Interfaz para el bean de clientes
- **ProductoViewModel**: Interfaz para el bean de productos
- **FacturaViewModel**: Interfaz para el bean de facturas

---

## 4. CAPA DE BASE DE DATOS

### 4.1 DatabaseConnection.java (Patrón Singleton)
- Implementa el patrón Singleton para una única conexión a la base de datos
- Utiliza SQLite como motor de base de datos
- Archivo: `sistemafacturacion.db`
- Inicializa automáticamente todas las tablas necesarias con su primera instancia

### 4.2 Tablas Creadas
1. **Roles** - Gestión de roles del sistema
2. **Usuarios** - Almacenamiento de usuarios con relación a Roles
3. **Clientes** - Información de clientes
4. **Productos** - Catálogo de productos
5. **CAI** - Registro de CAI y rangos de facturación
6. **Facturas** - Facturas electrónicas
7. **DetalleFacturas** - Detalles de productos en facturas
8. **HistorialTransacciones** - Auditoría de operaciones

### 4.3 Implementaciones Repository
Ubicadas en `database/`:
- **UsuarioRepositoryImpl**: Implementa UsuarioRepository con operaciones CRUD y búsqueda por nombre
- **ClienteRepositoryImpl**: Implementa ClienteRepository con búsqueda por RTN y criterios generales
- **ProductoRepositoryImpl**: Implementa ProductoRepository con actualización de stock
- **FacturaRepositoryImpl**: Implementa FacturaRepository con filtros complejos
- **CAIRepositoryImpl**: Implementa CAIRepository con obtención de CAI activo

---

## 5. CAPA DE SERVICIOS (LÓGICA DE NEGOCIO)

Ubicados en `services/`:

### 5.1 UsuarioService
- Implementa UsuarioInteractor
- Funciones: Registro con validación de duplicados, autenticación, gestión de usuarios, cambio de contraseña

### 5.2 ClienteService
- Implementa ClienteInteractor
- Funciones: Registro con validación de RTN único, búsqueda, actualización y eliminación

### 5.3 ProductoService
- Implementa ProductoInteractor
- Funciones: Gestión de productos, verificación y reducción de stock

### 5.4 FacturaService
- Implementa FacturaInteractor
- Funciones: Creación de facturas, cálculo automático de totales (subtotal, descuento, impuesto)

### 5.5 CAIService
- Implementa CAIInteractor
- Funciones: Gestión de CAI, obtención del siguiente número de factura, validación de rangos

---

## 6. CAPA DE PRESENTACIÓN (MANAGED BEANS)

Ubicados en `beans/`:

### 6.1 LoginBean
- Maneja la autenticación de usuarios
- Integración con sesiones HTTP
- Método: `autenticar()`, `cerrarSesion()`

### 6.2 UsuarioBean
- Implementa UsuarioViewModel
- Funciones: Crear, actualizar, eliminar usuarios
- Manejo de mensajes de éxito y error con Primefaces

### 6.3 ClienteBean
- Implementa ClienteViewModel
- Funciones: Gestión CRUD de clientes
- Búsqueda de clientes por criterios

### 6.4 ProductoBean
- Implementa ProductoViewModel
- Funciones: Gestión CRUD de productos
- Manejo de precios y stock

### 6.5 FacturaBean
- Implementa FacturaViewModel
- Funciones: Creación y gestión de facturas
- Cálculo de totales automático
- Métodos para generar PDF (preparado para implementación futura)

### 6.6 CAIBean
- Gestión de CAI y rangos de facturación
- Funciones CRUD para CAI
- Validación de rangos de facturación

---

## 7. VISTAS (PÁGINAS XHTML)

### 7.1 login.xhtml
- Interfaz de autenticación
- Diseño moderno con gradiente de colores
- Validación de campos requeridos
- Integración con LoginBean

### 7.2 index.xhtml
- Página de redirección a login.xhtml
- Primera página que se carga al acceder al sistema

### 7.3 inicio.xhtml
- Dashboard principal después del login
- Navegación a todos los módulos del sistema
- Interfaz con tarjetas de módulos

### 7.4 usuarios.xhtml
- Pantalla de gestión de usuarios
- Formulario para agregar nuevos usuarios
- Tabla con búsqueda y paginación
- Operaciones: Crear, Editar, Eliminar

### 7.5 clientes.xhtml
- Pantalla de gestión de clientes
- Formulario completo con todos los campos
- Tabla con información de clientes
- Búsqueda y paginación

### 7.6 productos.xhtml
- Pantalla de gestión de productos
- Formulario con campos de nombre, descripción, precio y stock
- Tabla de productos con conversión de moneda
- Validación de campos numéricos

### 7.7 facturas.xhtml
- Pantalla de creación y gestión de facturas
- Cálculo automático de totales
- Tabla de facturas con filtros
- Opciones para generar PDF

### 7.8 cai.xhtml
- Pantalla de gestión de CAI y rangos de facturación
- Formulario para registrar nuevos CAI
- Campos de fecha con calendario Primefaces
- Tabla con información de CAI activos e inactivos

---

## 8. TECNOLOGÍAS UTILIZADAS

### 8.1 Backend
- **Lenguaje**: Java 25
- **Arquitectura**: MVC con Managed Beans
- **Patrón de Conexión**: Singleton (DatabaseConnection)
- **Base de Datos**: SQLite
- **Driver**: org.sqlite.jdbc

### 8.2 Frontend
- **Framework**: JSF (Jakarta Faces)
- **Componentes**: Primefaces 15.0.16
- **Markup**: XHTML
- **Namespace**: Jakarta (jakarta.faces.*, jakarta.inject.*)

### 8.3 Build Tool
- **Maven**: Configurado en pom.xml

---

## 9. CARACTERÍSTICAS PRINCIPALES IMPLEMENTADAS

✅ **Autenticación de Usuarios**: Sistema de login con validación  
✅ **Gestión de Usuarios**: Crear, editar, eliminar usuarios del sistema  
✅ **Gestión de Clientes**: Registro y administración de clientes con RTN único  
✅ **Gestión de Productos**: Catálogo de productos con precios y stock  
✅ **Gestión de CAI**: Administración de CAI y rangos de facturación  
✅ **Gestión de Facturas**: Creación de facturas electrónicas con cálculo automático  
✅ **Base de Datos SQLite**: Almacenamiento seguro y confiable  
✅ **Interfaz Moderna**: Diseño responsivo con Primefaces  
✅ **Paginación**: Tablas con búsqueda y paginación  
✅ **Validación**: Validación de formularios en cliente y servidor  
✅ **Mensajes**: Sistema de mensajes de éxito y error  
✅ **Sesiones**: Gestión de sesiones de usuario  

---

## 10. ARCHIVO WEB.XML

El archivo `web.xml` está configurado con:
- Servlet JSF (Faces Servlet)
- Mapeo de URLs: `*.xhtml`
- Archivo de bienvenida: `index.xhtml`
- Tema de Primefaces: vela-blue

---

## 11. PRÓXIMAS CARACTERÍSTICAS A IMPLEMENTAR

- Generación de PDF para facturas
- Cierre de ventas diarias
- Reportes de ventas
- Gestión de devoluciones y notas de crédito
- Gestión de inventario
- Gestión de promociones y descuentos
- Historial completo de transacciones
- Control de permisos por roles

---

## 12. INSTRUCCIONES DE USO

### 12.1 Compilación
```bash
mvn clean install
```

### 12.2 Ejecución
El proyecto se ejecuta en un servidor de aplicaciones que soporte Jakarta EE 10 (Tomcat 10+, WildFly, etc.)

### 12.3 Acceso
1. Acceder a `http://localhost:8080/sistemafacturacion/`
2. Se redirige automáticamente a `login.xhtml`
3. Usar las credenciales para iniciar sesión
4. Acceder a los módulos desde el dashboard

---

## 13. NOTAS IMPORTANTES

- La base de datos SQLite se crea automáticamente en la primera ejecución
- Los datos se persisten en el archivo `sistemafacturacion.db`
- El sistema está diseñado para cumplir con los requisitos fiscales de CAI/RTN
- Todas las tablas tienen relaciones FK apropiadas
- Se implementó el patrón Singleton para optimizar conexiones a BD
- Se utilizan PreparedStatements para prevenir SQL Injection

---

**Proyecto completado: Sistema de Facturación con CAI/RTN**  
**Fecha: Agosto 2026**  
**Versión: 1.0**
