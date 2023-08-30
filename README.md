# piley server

Self-hosted backend for uploading and synchronizing [piley](https://github.com/justdeko/piley) tasks.

This service supports the following features:

* Creating and managing user accounts
* Uploading and updating backups for user accounts

## Prerequisites

To deploy or debug the service, you need the following tools:

* docker
* docker-compose
* postgres or some sql db query engine (if you want to debug the db)
* some JDK

That's it!

## How to deploy

To deploy the service locally, you can either run it standalone, or use docker-compose.

### Standalone

To simply run the application in debug mode, use an IDE and run it with any configuration you want.

Otherwise, there is the option to run a jar:

First, run the `buildJar.sh` script. With the resulting jar, run the following command:

    java -jar build/libs/piley-server-all.jar -port=8080

The database needs to be deployed separately, either as a standalone service or as a docker container.
Make sure, you change the `DB_BASE_URL` system variable if you use a base url other than `localhost`.
The service needs a running postgres database to function.

### Docker

With docker, you also first need to build the jar that the dockerfile will use:

    buildJar.sh

Then, you can just run

    docker-compose up -d

to deploy the database, the service and the reverse proxy server all at once.

The default port of the server is 8080, if you use it with localhost certificates and docker, you can also access it
via `https://localhost/

## Usage and Customization

To use the server in combination with the app, you need to expose it using a cloud provider or private server.
Make sure to generate or add your ssl certificate and key to the following location:

    nginx/ssl

they both should be named `localhost.crt` and `localhost.key` respectively. If you want to use other names, you need to
adapt the `nginx.conf` file.

If you want to add new user properties, checck out the `app/piley/model/User.kt` class and
also `app/piley/routes/UserRoutes.kt` for api endpoints. Don't forget to modify the DAO and its implementation
under `app/piley/dao` as well.

Aside from extending the code, you can also customize the database base url inside `docker-config.env`, which is `db` (
the base url inside a network) by default.

## Monitoring

If you want to connect your application to a monitoring service for health checks or similar, you can make a `GET`
request to the base service url. Otherwise, there is a swagger UI available under `<base-url>/swagger` for debugging
purposes.

## License

[MIT](https://github.com/justdeko/pile-server/blob/main/LICENSE)
