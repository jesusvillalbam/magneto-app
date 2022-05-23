# API de Reclutamiento de mutantes

La aplicación permite reclutar la mayor cantidad de mutantes para poder luchar
contra los X-Men. Se usa la secuencia de ADN de cada humano para verificar si es mutante o no.

## Requerimientos

Para construir y ejecutar la aplicación se requiere:

- [JDK 11](https://www.oracle.com/co/java/technologies/javase/jdk11-archive-downloads.html)

## Ejecutar la aplicación localmente

Para correr la aplicación localmente, se situa en la raiz del proyecto y luego se ejecuta el siguiente comando: 
```shell
./gradlew bootRun
```

## Servicios Web REST

### ¿Humano es mutante?

- Permite verificar si un humano es mutante, de acuerdo a su secuencia de ADN, cada segmento del ADN puede únicamente contener las letras (A,T,C,G). Un humano es mutante, si se encuentra más de una secuencia de cuatro letras
  iguales, de forma oblicua, horizontal o vertical.

#### Request    
  ```shell
  curl --request POST \
  --url http://54.236.52.207:8080/api/v1/recruitment/mutant \
  --header 'Content-Type: application/json' \
  --cookie JSESSIONID=194757B46C74079EFB111970740C60D2 \
  --data '{
  "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGACGG", "CCTCTA", "TCACTG"]
  }'
  ```

#### Response
  ```shell
{
	"mutant": true
}
  ```
- Si la respuesta es un código HTTP 200, el humano es mutante, en cambio si la respuesta es HTTP 403, el humano no es mutante.
- En caso que se presenten errores de negocio tales como, enviar información que no es correcta para realizar la validación, se responde HTTP 400 con un mensaje descriptivo del error.

### Estádisticas
    
- Permite obtener las estádisticas correspondientes al promedio de mutantes reclutados de acuerdo a los humanos que se han postulado en el proceso.
#### Request
  ```shell
  curl --request GET \
  --url http://54.236.52.207:8080/api/v1/recruitment/stats \
  ```
#### Response
  ```shell
{
	"count_mutant_dna": 3,
	"count_human_dna": 5,
	"ratio": 0.6
}
  ```

## Swagger API Doc
Para visualizar la documentación de la aplicación más detallada y de igual forma poder consumir los servicios, debe ingresar mediante la siguiente URL:
  ```shell
http://54.236.52.207:8080/swagger/index.html
  ```
## Docker - Contenerización
La API cuenta con un archivo Dockerfile, que permite construir la imagen de la API y poder crear y ejecutar el contenedor para la posterior ejecución del microservicio.

Para construir la aplicación, primero se limpia y se contruye la aplicación:
```shell
./gradlew clean build
```

Luego procedemos a crear la imagen de Docker
```shell
docker build -t magneto-recruitment-ms:202205231408 .
```
Finalmente ejecutamos la imagen para obtener el contenedor el cual estará expuesto en el puerto 8080
```shell
docker run -p 8080:8080 magneto-recruitment-ms:202205231408
```
