# RemoteDataSource

## Purpose

- RemoteDataSource класс создается для походов в сеть через HttpClient

## Constructor Rules

- RemoteDataSource содержит внутри себя исключительно методы обертки для походов в сеть
- инжектит себе в конструктор HttpClientBuilder, который будет потом локально собран в client

## Method Rules

- В каждом методе может быть только один запрос в сеть, нельзя запихнуть несколько в один метод
- Все методы должны возвращать сериализованный body запроса или HttpResponse (если body не требуется)

## Error Handling

- На уровне RemoteDataSource не должно быть никакой обработки ошибок. Ошибки обрабатываются на других уровнях

## Examples

- Правильный пример:
    ```kotlin
    @Inject
    internal class AuthRemoteDataSource(
        builder: HttpClientBuilder,
    ) {
        private val client: HttpClient = builder.configureBaseClient()
            .build()
    
        suspend fun postCode(body: CodeBodyDTO): CodeResponseDTO {
            return client.post("/auth/v2/code") {
                setBody(body)
                setRequestAsStartOfUserAuthV3Flow()
            }.body<CodeResponseDTO>()
        }
    
        // иногда можно возвращать HttpResponse, зависит от требований к ручке. Если не нужен ответ, например, а только его статус
        suspend fun postAuth(body: AuthBodyDTO): HttpResponse {
            return client.post("/auth/v2/auth") {
                setBody(body)
            }
        }
    }
    ```
- Неправильный пример:
    ```kotlin
    // нет аннотации @Inject, хотя должна быть
    internal class AuthRemoteDataSource(
        // инжектится сразу HttpClient, так нельзя, должен инжектиться builder
        client: HttpClient,
    ) {
    
        suspend fun postCode(body: CodeBodyDTO): HttpResponse {
            // два запроса в одном методе, так нельзя
            client.post("/auth/v2/code") {
                setBody(body)
                setRequestAsStartOfUserAuthV3Flow()
            }.body<CodeResponseDTO>()
            client.post("/auth/v2/auth") {
                setBody(body)
            }
        }
    }
    ```
