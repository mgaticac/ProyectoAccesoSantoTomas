# Proyecto 'Ingreso Santo Tomas'
Este es un proyecto para tener un registro/flujo de personas a la institución utilizando un lector de huella digital, [Referencia del sensor](https://neurotechnology.com/fingerprint-scanner-digitalpersona-u-are-u-4500.html)

![Sensor](https://images-eu.ssl-images-amazon.com/images/I/41qsrN3tR0L._SX300_QL70_.jpg)

## 🖥 Instalación
**Nota antes de su instalación**:
los SO soportados por el SDK de **digitalpersona u.are.u.4500** son solo distrubuciones de windows:
*Microsoft Windows XP (32/64-bit), Microsoft Windows XP Embedded (32-bit), Microsoft Windows Vista (32/64-bit), Microsoft Windows 7 (32/64-bit), Microsoft Server 2003/2008 (32/64-bit)*

El lenguaje es Java por tanto debe utilizarte almenos **Java SE 8** para su uso.

*opcional para compilación automatica: luego de instalar JDK O JRE debe asignarse una variable de entorno JAVA_HOME con la ruta de la instalación del JDK/JRE*

### 📁Clonar el repositorio
Con esto obtemos el proyecto y las dependencias (ya que todas estan en el mismo repositorio)

`git clone https://github.com/XZeromarx/ProyectoAccesoSantoTomas`

Para la instalación debe instalarse el SDK del lector *DigitalPersona U.are.U 4500 fingerprint reader* encontrados en el [proyecto de github](https://github.com/XZeromarx/ProyectoAccesoSantoTomas/tree/master/drivers%20fp/sdk-fp), Ejecutando el siguiente archivo: `ProyectoAccesoSantoTomas/drivers fp/sdk-fp/SDK/Setup.exe`

### 🗃 Compilando el proyecto
El proyecto es un **proyecto de Netbeans utilizando Apache ANT**, por lo tanto puede generar el ejecutable utilizando el IDE o usando Apache ANT directamente

#### 🐜 Usando Apache ANT (Opcional)
Descargar los [binarios](https://ant.apache.org/bindownload.cgi) y luego dejar la ruta de la carpeta bin como **Variable de entorno (PATH)**. [Referencias](https://medium.com/@01luisrene/como-agregar-variables-de-entorno-s-o-windows-10-e7f38851f11f) sobre variables de entorno 
ejemplo ruta:

> C:\apache-ant-1.10.9\bin

luego en la raíz del proyecto escribir el siguiente comando:

```SH
ant 
```
con esto se generará el directorio **/dist** en la raiz del proyecto, dentro de este se encontrara el ejecutable que corresponde a el software.

### 📄 Generando archivo de configuración
un ejemplo del archivo de configuración nombrado `config.properties` en la raiz del ejecutable (dentro de dist si se siguieron los pasos anteriores) del proyecto consta de las siguientes entradas obligatorias:

```
#Database config
db.host = host_destino_base_de_datos
db.name = nombre_base_de_datos
db.user = nombre_usuario_base_de_datos
db.passwd = contraseña_base_de_datos

#Id institulo
location.institute = id_ubicacion_instituto
```

el Atributo **location.institute** del archivo de configuración es un id numerico que por ahora depende directamente del ID la base de datos, para obtener la información de consultar directamente a esta

```sql
USE fpdb;
SELECT id,name FROM institute;
```
*nota: por razones de seguridad este archivo debe ser creado manualmente*


### 🧢 Poblando/Configurando la Base de datos 
La aplicación accede a una base de datos **MySQL/MariaDB** por lo cual se debe tener los datos de conexión de esta para luego poblarla.

El script para poblar la BD se encuentra en :  [Link SQL Script](https://github.com/XZeromarx/ProyectoAccesoSantoTomas/blob/master/src/model/database.sql) 📃

## 🚀 Ejecutando la aplicación
Para ejecutar la aplicación mostrando CLI:
```sh
java -jar "IngresoSantoTomas.jar"
```
Ejecución sin CLI
```sh
javaw -jar IngresoSantoTomas.jar
```

# 📕 Licencia 
Este proyecto creado por [klawx3](https://github.com/klawx3) y [xzeromarx](https://github.com/XZeromarx) esta bajo la licencia:
> GNU GENERAL PUBLIC LICENSE
> Version 3, 29 June 2007
