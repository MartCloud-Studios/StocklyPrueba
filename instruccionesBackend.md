## herramientas necesarias
- jdk 25
- MariaDB

## configuración de la base de datos
en el archivo aplication.properties: StocklyBackend\src\main\resources\application.properties
agregar agregar las variables de entorno necesarias:

spring.datasource.password=${DB_PASSWORD}
app.jwt.secret=${JWT_SECRET} # clave secreta para firmar tokens de 32 bytes

spring.datasource.url=jdbc:mariadb://localhost:3307/stockly_db # verificar que el puerto del localhost es correcto

## instrucciones de arranque del backend
crear una terminal o desplazarse a la carpeta StocklyBackend y ejecutar el siguiente comando:

```
.\mvnw.cmd spring-boot:run
```
para que se ejecute el cliente mobil: 
```
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8080 tcp:8080
```
```
& "E:\Programas\SDK\platform-tools\adb.exe" reverse tcp:8080 tcp:8080
```
este conjuro en power shell crea una clave de 32bytes. es para poder firmar los tokens.
```
[guid]::NewGuid().ToString("N")
```

Ejecutar swagger:

http://localhost:8080/swagger-ui/index.html
