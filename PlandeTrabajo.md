# Plan de Trabajo — Sistema de Facturación

## Información General

| Campo          | Detalle                                      |
|----------------|----------------------------------------------|
| **Proyecto**   | Sistema de Facturación                       |
| **Tecnología** | Jakarta EE 10, JSF 4, PrimeFaces 15, SQLite  |
| **Servidor**   | Apache Tomcat 10                             |
| **Build**      | Maven 3.9                                    |

---

## Módulos del Sistema

### 1. Autenticación (`login.xhtml` + `LoginBean`)
- Formulario de login con usuario y contraseña.
- Al autenticar, el usuario se guarda en la sesión HTTP como `usuarioAutenticado`.
- El `LoginBean` redirige a `inicio.xhtml` si las credenciales son correctas.
- El cierre de sesión invalida la sesión HTTP y redirige al login.

---

### 2. Inicio / Dashboard (`inicio.xhtml`)
- Panel de tarjetas de navegación hacia cada módulo del sistema.
- Las tarjetas de **Usuarios** y **CAI** sólo son visibles para el rol administrador
  gracias a `rendered="#{sessionBean.admin}"`.
- Contiene acceso a: Clientes, Productos, Facturas, Historial de Facturas, CAI y Usuarios.

---

### 3. Gestión de Clientes (`clientes.xhtml` + `ClienteBean`)
- Registro, edición y eliminación de clientes.
- Campos: nombre, RTN, email, teléfono, dirección, ciudad.
- Tabla con paginación, ordenamiento y filtrado.
- Confirmación de eliminación vía `p:confirmDialog`.

---

### 4. Gestión de Productos (`productos.xhtml` + `ProductoBean`)
- CRUD completo de productos del catálogo.
- Campos: nombre, descripción, precio de venta, stock, estado.
- Control de stock integrado con la creación de facturas.

---

### 5. Gestión de CAI / RTN (`cai.xhtml` + `CAIBean`)
- Registro y administración de Códigos de Autorización de Impresión (CAI).
- Campos: código CAI, RTN, rango inicial, rango final, fechas de emisión y vencimiento.
- **Acceso restringido:** solo el administrador puede acceder; los demás usuarios ven
  un panel de "Acceso Denegado".
- Guard a nivel de `@PostConstruct` en el bean y `rendered` en la vista.

---

### 6. Gestión de Facturas (`facturas.xhtml` + `FacturaBean`)
- Creación de facturas con detalle de productos (líneas).
- AutoComplete para selección de cliente y producto.
- Cálculo automático de subtotal, descuento e ISV (15%) en tiempo real con AJAX.
- Validación de stock antes de agregar líneas.
- Validación de rango de factura contra el CAI activo.
- Tabla de facturas registradas con acciones: ver detalle y descargar PDF.
- **Corrección aplicada:** `update=":formFactura:detalleDialog"` (ruta absoluta) para
  resolver `ComponentNotFoundException` causada por `scrollable="true"` en la DataTable.

---

### 7. Historial de Facturas (`historial_facturas.xhtml` + `FacturaBean`)
- Tabla completa de todas las facturas con columnas: N° Factura, CAI, Fecha, **Cliente**,
  Subtotal, Descuento %, ISV (15%), Total.
- Filtros por columna (N° Factura, CAI, Cliente).
- Botón "Ver detalle" que abre un diálogo con:
  - Encabezado: N° Factura, Fecha, Cliente, CAI.
  - Tabla de productos (nombre, cantidad, precio unitario, subtotal por línea).
  - Resumen financiero: Subtotal → − Descuento → + ISV → **Total**.
- Botón de acceso desde `facturas.xhtml` (toolbar) y desde `inicio.xhtml` (tarjeta).

---

### 8. Gestión de Usuarios (`usuarios.xhtml` + `UsuarioBean`)
- CRUD completo de usuarios del sistema.
- Selector de **Rol** en el formulario: Administrador (idRol=1) o Usuario (idRol=2).
- Columna de **Rol** en la tabla con etiqueta visual (`p:tag`).
- **Acceso restringido:** solo el administrador puede acceder.
- Guard a nivel de `@PostConstruct` en el bean y `rendered` en la vista.

---

### 9. Generación de PDF (`FacturaService.generarPDF`)
- Implementado con la librería **OpenPDF 1.3.43**.
- El PDF incluye:
  - Encabezado con fondo azul: "FACTURA" y N° de factura.
  - Datos del cliente (nombre, RTN, email).
  - Tabla de productos (nombre, cantidad, precio unitario, subtotal).
  - Resumen financiero (subtotal, descuento, ISV, total).
  - Pie de página con el código CAI.
- La descarga se realiza mediante `ExternalContext.getResponseOutputStream()`
  con el header `Content-Disposition: attachment`.
- El botón en la vista usa `ajax="false"` para que el navegador reciba la descarga.

---

### 10. Control de Acceso por Roles (`SessionBean`)
- Bean `@RequestScoped` que lee el usuario autenticado de la sesión HTTP.
- Expone `#{sessionBean.admin}` para verificar si el usuario tiene rol administrador.
- La base de datos se inicializa automáticamente con:
  - Roles: Admin (idRol=1), Usuario (idRol=2).
  - Usuario por defecto: `admin / admin123`.

---

## Correcciones y Mejoras Realizadas

| # | Problema / Tarea                            | Solución Aplicada                                                                  |
|---|---------------------------------------------|------------------------------------------------------------------------------------|
| 1 | `ComponentNotFoundException: detalleDialog` | Cambio a ruta absoluta `:formFactura:detalleDialog` + `oncomplete` para show dialog |
| 2 | PDF no funcional (`return new byte[0]`)     | Implementación completa con OpenPDF + descarga vía `ExternalContext`               |
| 3 | Historial sin datos del cliente             | JOIN en `obtenerTodas()` + campo `nombreCliente` en `Factura`                      |
| 4 | Historial sin ISV ni descuento              | Métodos `getMontoDescuento()` y `getMontoISV()` en `Factura`                       |
| 5 | Sin control de acceso por rol               | `SessionBean` + guards en beans + `rendered` en vistas                             |
| 6 | Nuevos usuarios con rol admin por defecto   | `idRolNuevo = 2` (Usuario) por defecto; selector de rol en formulario              |
| 7 | Sin botón de Historial accesible            | Botón en toolbar de `facturas.xhtml` + tarjeta en `inicio.xhtml`                  |
| 8 | Sin seed de roles ni admin en la BD         | Seed automático en `DatabaseConnection.inicializarBaseDatos()`                     |

---

## Dependencias Principales

```xml
<!-- PrimeFaces 15 -->
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>15.0.16</version>
    <classifier>jakarta</classifier>
</dependency>

<!-- SQLite JDBC -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.50.3.0</version>
</dependency>

<!-- OpenPDF (generación de PDF) -->
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.43</version>
</dependency>

<!-- Weld CDI -->
<dependency>
    <groupId>org.jboss.weld.servlet</groupId>
    <artifactId>weld-servlet-shaded</artifactId>
    <version>6.0.3.Final</version>
</dependency>
```

---

## Credenciales por Defecto

| Usuario | Contraseña | Rol           |
|---------|------------|---------------|
| admin   | admin123   | Administrador |

> Los nuevos usuarios creados desde el sistema tienen rol **Usuario** por defecto y no pueden acceder a la gestión de CAI ni de Usuarios.
