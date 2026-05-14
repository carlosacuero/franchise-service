# API Franquicias (Spring WebFlux + MongoDB reactivo)

API reactiva para gestionar **franquicias**, **sucursales** y **productos** con stock, persistida en **MongoDB**.

## Requisitos

- **JDK 17**
- **Maven** (o el wrapper `mvnw` / `mvnw.cmd` incluido)
- **Docker** (opcional, para MongoDB en contenedor, pruebas de integración e imagen de la aplicación)

## Ejecución local

### 1. Levantar MongoDB

Con Docker:

```bash
docker run -d --name franquicias-mongo -p 27017:27017 mongo:7.0
```

O con **docker compose** (API + Mongo):

```bash
docker compose up --build
```

La API quedará en `http://localhost:8081`. El compose define `MONGODB_URI=mongodb://mongo:27017/franquicias_db`.

### 2. Solo la aplicación (Mongo ya corriendo en localhost:27017)

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

Variables útiles:

| Variable      | Descripción                    | Por defecto                               |
|---------------|--------------------------------|-------------------------------------------|
| `MONGODB_URI` | URI de conexión a MongoDB      | `mongodb://localhost:27017/franquicias_db` |

## Infraestructura como código (Terraform)

En `infra/terraform` hay un ejemplo que provisiona **MongoDB en Docker** (proveedor `kreuzwerker/docker`).

```bash
cd infra/terraform
terraform init
terraform apply
```

Copie el `mongo_connection_string` del output en `MONGODB_URI` o en `application.properties` si cambia el puerto.

## Endpoints (`/api/v1`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/franquicias` | Crear franquicia (`{"nombre":"..."}`) |
| `GET` | `/franquicias/{franquiciaId}` | Obtener franquicia |
| `POST` | `/franquicias/{franquiciaId}/sucursales` | Agregar sucursal |
| `POST` | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos` | Agregar producto (`nombre`, `stock`) |
| `DELETE` | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}` | Eliminar producto |
| `PATCH` | `.../productos/{productoId}/stock` | Actualizar stock (`{"stock":n}`) |
| `GET` | `/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal` | Por cada sucursal, el producto con mayor stock (empate por nombre); indica sucursal |
| `PATCH` | `/franquicias/{franquiciaId}/nombre` | Actualizar nombre de franquicia |
| `PATCH` | `/franquicias/{franquiciaId}/sucursales/{sucursalId}/nombre` | Actualizar nombre de sucursal |
| `PATCH` | `.../productos/{productoId}/nombre` | Actualizar nombre de producto |

Prefijo base: `http://localhost:8081/api/v1`.

## Pruebas

Las pruebas de integración usan **Testcontainers** (requiere Docker):

```bash
./mvnw test
```

## Imagen Docker (solo JAR)

Tras `mvn package`:

```bash
docker build -t franquicias-api:local .
docker run --rm -p 8081:8081 -e MONGODB_URI=mongodb://host.docker.internal:27017/franquicias_db franquicias-api:local
```

(Ajuste `MONGODB_URI` según su red; en Linux puede usar la IP del host en lugar de `host.docker.internal`.)

## Despliegue en la nube (orientación)

1. **MongoDB gestionado**: MongoDB Atlas, Amazon DocumentDB u otro compatible.
2. **Contenedor de la API**: construir y publicar la imagen en un registro (GHCR, ECR, ACR).
3. **Runtime**: AWS App Runner / ECS Fargate, Azure Container Apps, Google Cloud Run, etc.
4. Definir la variable de entorno `MONGODB_URI` apuntando al clúster gestionado.

No se incluye un pipeline concreto de nube en este repositorio; los pasos dependen del proveedor elegido.

## Git

Se recomienda flujo con ramas (`main`/`develop`, *feature branches*) y *pull requests* antes de integrar. Publique el repositorio en GitHub, Bitbucket u otro servicio con visibilidad pública para la entrega de la prueba.

## Tecnologías

- Spring Boot 4, **WebFlux**, programación reactiva (`Mono` / `Flux`)
- **Spring Data MongoDB Reactive**
- Validación Jakarta Bean Validation
- Rutas funcionales (`RouterFunction` + `Handler`)
