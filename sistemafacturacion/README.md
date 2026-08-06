OBJETIVO

Sistema de facturacion con CAI/RTN, que permite a los usuarios generar facturas electrónicas de manera eficiente y 
cumplir con los requisitos legales establecidos por la autoridad fiscal. El sistema está diseñado para facilitar la 
gestión de ventas, clientes y productos, proporcionando un flujo de trabajo intuitivo y seguro tanto para el usuario 
final como para el administrador del sistema. La aplicacion permite la generación de facturas electrónicas con los datos 
requeridos por la autoridad fiscal, incluyendo el CAI (Código de Autorización de Impresión) y el RTN (Registro Tributario Nacional),
asegurando que todas las transacciones sean válidas y cumplan con la normativa vigente. La aplicacion proporcionara una interfaz amigable 
para la creación, edición y almacenamiento de facturas, así como la gestión de cliente y productos, mantenimiento de CAI y rangos de facturacion, 
permitiendo a los usuarios mantener un registro organizado de sus operaciones comerciales, asi mismo cierre de ventas diarias y reportes de ventas, 
generando un historial de transacciones que puede ser consultado en cualquier momento. 
En resumen, este sistema de facturación con CAI/RTN es una herramienta integral que optimiza el proceso de facturación electrónica, 
asegurando el cumplimiento legal y mejorando la eficiencia operativa de las empresas.

PANTALLAS

1. Pantalla de inicio de sesión: Permite a los usuarios ingresar al sistema utilizando sus credenciales(usuario y contraseña).
2. Pantalla de registro de clientes: Permite a los usuarios agregar, editar y eliminar clientes, así como buscar clientes existentes en la base de datos.
3. Pantalla de registro de productos: Permite a los usuarios agregar, editar y eliminar productos, así como buscar productos existentes en la base de datos. Aqui se le asignara el precio de venta.
4. Pantalla de generación de facturas: Permite a los usuarios crear nuevas facturas electrónicas, seleccionar clientes y productos, ingresar cantidades y generar la factura con el CAI y RTN correspondientes. Debe hacer los calculos correspondientes al subtotal, descuentos, impuestos y total de la factura, asegurando que los datos ingresados sean válidos y cumplan con los requisitos legales establecidos por la autoridad fiscal.Debe generarse la factura para su impresión.
5. Pantalla de gestión de CAI y rangos de facturación: Permite a los usuarios administrar los CAI y rangos de facturación, asegurando que las facturas generadas cumplan con los requisitos legales.
6. Pantalla de cierre de ventas diarias: Permite a los usuarios cerrar las ventas del día, generando un resumen de las transacciones realizadas y asegurando que todas las facturas estén registradas correctamente.
7. Pantalla de reportes de ventas: Permite a los usuarios generar reportes detallados de ventas, incluyendo información sobre clientes, productos, montos y fechas, facilitando el análisis de las operaciones comerciales y la toma de decisiones estratégicas.
8. Pantalla de historial de transacciones: Permite a los usuarios consultar el historial completo de facturas generadas, con opciones de búsqueda y filtrado por fecha, cliente o producto, proporcionando un registro organizado y accesible de todas las operaciones realizadas en el sistema.
9. Pantalla de gestión de usuarios y roles: Permite a los administradores del sistema crear, editar y eliminar usuarios, asignar roles y permisos específicos,cambiarles contraseña, garantizando que cada usuario tenga acceso únicamente a las funciones y datos necesarios para su desempeño, fortaleciendo la seguridad y el control del sistema de facturación con CAI/RTN.
10. Pantalla de gestión de devoluciones y notas de crédito: Permite a los usuarios registrar y procesar devoluciones de productos y emitir notas de crédito correspondientes, asegurando que las transacciones sean reflejadas correctamente en el sistema y cumplan con los requisitos legales, proporcionando un control adecuado sobre las operaciones comerciales y la relación con los clientes.
11. Pantalla de gestión de inventario: Permite a los usuarios monitorear y controlar el stock de productos, registrar entradas,salidas y realizar ajustes.
12. Pantalla de gestión de promociones y descuentos: Permite a los usuarios crear y administrar promociones y descuentos aplicables a productos o clientes específicos, incentivando la compra y fidelización de clientes, y asegurando que las transacciones sean reflejadas correctamente en las facturas generadas, cumpliendo con los requisitos legales establecidos por la autoridad fiscal.

BASE DE DATOS

La base de datos será en la tecnología SQLite y estará estructurada para almacenar información sobre los usuarios, clientes, productos, facturas, 
CAI, RTN, rangos de facturación, transacciones y reportes de ventas. A continuación se presenta un esquema básico de la base de datos:

TABLAS

1. Usuarios
2. Clientes
3. Productos
4. Facturas
5. CAI y Rangos de Facturación
6. Historial de Transacciones
7. Roles

LIBRERIAS Y TECNOLOGIAS

*   JSF con Primefaces 15.0.16 para frontend.
*   No utilizar ningun otro framework de frontend, unicamente controles de primefaces.
*   Java 25 para backend con estructura MVC (utilizando managed beans) y Singleton para conexión a base de datos (jdbc).

ESTRUCTURA DE CARPETAS

* src/main/java: Contendrá el código fuente de la aplicación, incluyendo los managed beans, controladores y modelos.
  - carpeta beans para los managed beans
  - carpeta data para los objetos java
  - carpeta database para la conexión a la base de datos y las operaciones CRUD.
  - carpeta services para la lógica de negocio y servicios de la aplicación.
* src/main/resources: Contendrá los archivos de configuración, como el archivo de propiedades para la conexión a la base de datos y otros recursos necesarios.
* src/main/webapp: Contendrá los archivos JSP, XHTML y otros recursos web, como imágenes y hojas de estilo CSS. 

