# Guía para Ejecutar el Sistema de Facturación

## Requisitos Previos

- Java 8 o superior (se recomienda Java 11+)
- Maven 3.6.0 o superior
- Servidor de Aplicaciones: Tomcat 10+, WildFly 25+, u otro compatible con Jakarta EE 10

## Pasos para Compilar y Ejecutar

### 1. Compilar el Proyecto

```bash
cd C:\Users\WilliamB\Documents\GitHub\PW2_PROYECTO_FINAL_3P\sistemafacturacion
mvn clean install
```

Este comando:
- Descarga todas las dependencias (incluyendo sqlite-jdbc)
- Compila el código
- Ejecuta las pruebas
- Genera el archivo WAR en `target/sistemafacturacion.war`

### 2. Desplegar en Tomcat

#### Opción A: Copiar el WAR

```bash
# Copiar el WAR al directorio webapps de Tomcat
cp target/sistemafacturacion.war %CATALINA_HOME%\webapps\
```

#### Opción B: Usar Maven Plugin

Primero, configura tu servidor en `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <url>http://localhost:8080/manager/text</url>
        <server>tomcat</server>
        <path>/sistemafacturacion</path>
        <username>admin</username>
        <password>admin</password>
    </configuration>
</plugin>
```

Luego ejecuta:

```bash
mvn tomcat7:deploy
```

### 3. Acceder a la Aplicación

Una vez desplegada, accede a:

```
http://localhost:8080/sistemafacturacion/
```

### 4. Credenciales de Prueba

**Usuario:** admin  
**Contraseña:** admin123

O

**Usuario:** usuario  
**Contraseña:** usuario123

## Solución de Problemas

### Error: "Cannot invoke prepareStatement because conn is null"

Este error ocurre si:

1. **El driver SQLite no está disponible**
   - Solución: Ejecutar `mvn clean install` nuevamente
   - Verificar que sqlite-jdbc esté en el classpath

2. **La base de datos no se encuentra**
   - La BD se crea automáticamente en la primera ejecución
   - Se busca en varias ubicaciones:
     - Directorio actual
     - systemafacturacion/sistemafacturacion.db
     - target/sistemafacturacion/sistemafacturacion.db
     - ${user.dir}/sistemafacturacion.db

3. **Permisos insuficientes**
   - Verificar que la carpeta del proyecto tiene permisos de escritura

### Verificar que el Driver está Disponible

```bash
mvn dependency:tree | grep sqlite
```

Debería mostrar:
```
org.sqlite:sqlite-jdbc:jar:3.44.0.0:compile
```

## Estructura del Proyecto

```
sistemafacturacion/
├── src/
│   ├── main/
│   │   ├── java/com/example/sistemafacturacion/
│   │   │   ├── beans/              (Managed Beans JSF)
│   │   │   ├── data/               (POJOs)
│   │   │   ├── database/           (Conexión y Repositories)
│   │   │   ├── interfaces/         (Contratos)
│   │   │   └── services/           (Lógica de Negocio)
│   │   ├── webapp/
│   │   │   ├── WEB-INF/web.xml    (Configuración JSF)
│   │   │   └── *.xhtml             (Vistas)
│   │   └── resources/              (Propiedades)
│   └── test/
├── target/                         (Generado por Maven)
├── pom.xml                         (Configuración Maven)
└── sistemafacturacion.db           (Base de datos SQLite)
```

## Notas Importantes

- **Base de Datos:** SQLite es portátil y file-based. La BD se crea automáticamente.
- **Seguridad:** Las contraseñas en la BD de prueba son en texto plano. Para producción, implementar BCrypt.
- **Logging:** Se registran eventos importantes en la consola y logs.
- **Compatibilidad:** Compatible con Java 8+ y Jakarta EE 10.

## Contacto y Soporte

Para problemas de conexión a BD o autenticación, revisar:
- `DatabaseConnection.java` - Lógica de conexión
- `UsuarioRepositoryImpl.java` - Queries de usuario
- Logs de la consola del servidor
