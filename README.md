# HttpClient Module

This repository provides the configurable http client. Just change the the property of your http client's in the data source in which you are keeping all the properties and the library will create a new http client and it will replace in your HttpClients Store in the code.

You need to take care of mainly below items to use the library:
- Import or copy http client maven module to your project.
- Http Client Initializer
- Http client properties in your configuration.
- Main HttpClient Class which does the magic of interaction with the services.


### HttpClient Initializer
```java
@Component
public class TouristHttpClientInitializer extends HttpClientInitializer {

  private final Environment environment;

  @Autowired
  public TouristHttpClientInitializer(Environment environment) {
    this.environment = environment;
  }

  @PostConstruct
  private void init() {
    refreshClient();
  }

  // refresh this http client from some scheduler to refresh client according to your requirements.
  public void refreshClient() {
    log.info("Refreshing TOURIST - Http Client");
    initializeHttpClient(TOURIST.name());
    log.info("Http Client TOURIST refreshed");
  }

  @Override
  public String providePropertyValue(String propertyName) {
    // return value for the properties from your data source: DB/CACHE/SPRING, etc
    return environment.getProperty(propertyName);
  }
}
```

------------

### Http client properties in your configuration.
Comma separated all http client names in below property:
- http.client.names=TOURIST,GOOGLE_APIS

Individual Http Client's property can be defined according to below:

- http.client.**TOURIST**.socket.timeout=2000
- http.client.**TOURIST**.connection.timeout=1000
- http.client.**TOURIST**.connection.request.timeout=500
- http.client.**TOURIST**.max.connections=50
- http.client.**TOURIST**.max.per.channel=20
- http.client.**TOURIST**.connection.validate.inactivity.period=2000

- http.client.**GOOGLE_APIS**.socket.timeout=2000
- http.client.**GOOGLE_APIS**.connection.timeout=1000
- http.client.**GOOGLE_APIS**.connection.request.timeout=500
- http.client.**GOOGLE_APIS**.max.connections=50
- http.client.**GOOGLE_APIS**.max.per.channel=20
- http.client.**GOOGLE_APIS**.connection.validate.inactivity.period=2000

Below are the example Urls configured for this client:
- tourist.fetch-tourists-url=http://restapi.adequateshop.com/api/Tourist?page=1
- tourist.create-tourists-url=http://restapi.adequateshop.com/api/Tourist


------------


### Main HttpClient Class

```java
@Component
@Slf4j
public class TouristHttpClient {

  private static final long HTTP_CLIENT_COMMUNICATION_RETRY_BACKOFF = 1000l;

  private final HttpClient httpClient;
  private final TouristConfig touristConfig;

  @Autowired
  public TouristHttpClient(HttpClient httpClient, TouristConfig touristConfig) {
    this.httpClient = httpClient;
    this.touristConfig = touristConfig;
  }

  @Retryable(
      value = {Exception.class},
      maxAttemptsExpression = "3",
      backoff = @Backoff(delay = HTTP_CLIENT_COMMUNICATION_RETRY_BACKOFF),
      listeners = "genericReadTimeoutRetryHttpListener",
      recover = "createTouristRecover")
  public Tourist createTourist(CreateTouristRequest requestDto) {
    log.info("Calling create tourist api");

    Map<String, String> headers = createHeaders();
    return httpClient.request(
        TOURIST.name(),
        POST,
        touristConfig.getCreateTouristsUrl(),
        requestDto,
        headers,
        Tourist.class);
  }

  @Recover
  private String createTouristRecover(Throwable throwable, CreateTouristRequest requestDto) {
    log.info("Reached recover method for create tourist");
    if (throwable instanceof ResourceAccessException
        && throwable.getCause() instanceof SocketTimeoutException) {
      log.info("CREATE_TOURIST_READ_TIMEOUT: {}");
    } else if (throwable instanceof ResourceAccessException
        && (throwable.getCause() instanceof ConnectTimeoutException || throwable.getCause() instanceof ConnectException)) {
      log.info("CREATE_TOURIST_CONNECTION_FAILED: {}");
    }

    if (throwable instanceof HttpClientErrorException) {
      // 4xx
      HttpClientErrorException exception = (HttpClientErrorException) throwable;
      log.info("CREATE_TOURIST_FAILED_{}_ERROR", exception.getRawStatusCode());

    } else if (throwable instanceof HttpServerErrorException) {
      // 5xx
      HttpServerErrorException exception = (HttpServerErrorException) throwable;
      log.info("CREATE_TOURIST_FAILED_{}_ERROR", exception.getRawStatusCode());
    } else {
      log.info("CREATE_TOURIST_FAILED");
    }
    return null;
  }

  @Retryable(
      value = {Exception.class},
      maxAttemptsExpression = "3",
      backoff = @Backoff(delay = HTTP_CLIENT_COMMUNICATION_RETRY_BACKOFF),
      listeners = "genericReadTimeoutRetryHttpListener",
      recover = "getTouristRecover")
  public TouristResponse getTourist() {

    log.info("Fetching all users");

    Map<String, String> headers = createHeaders();

    return httpClient.request(
        TOURIST.name(),
        GET,
        touristConfig.getFetchTouristsUrl(),
        null,
        headers,
        TouristResponse.class);
  }

  @Recover
  private TouristResponse getTouristRecover(Throwable throwable) {
    log.info("Reached recover method for get users");
    if (throwable instanceof ResourceAccessException
        && throwable.getCause() instanceof SocketTimeoutException) {
      log.info("GET_USERS_READ_TIMEOUT");

    } else if (throwable instanceof ResourceAccessException
        && (throwable.getCause() instanceof ConnectTimeoutException || throwable.getCause() instanceof ConnectException)) {
      log.info("GET_USERS_CONNECTION_FAILED");
    }
    if (throwable instanceof HttpClientErrorException) {
      // 4xx
      HttpClientErrorException exception = (HttpClientErrorException) throwable;
      log.info("GET_USERS_FAILED_{}_ERROR", exception.getRawStatusCode());

    } else if (throwable instanceof HttpServerErrorException) {
      // 5xx
      HttpServerErrorException exception = (HttpServerErrorException) throwable;
      log.info("GET_USERS_FAILED_{}_ERROR", exception.getRawStatusCode());
    } else {
      log.info("GET_USERS_FAILED");
    }
    return null;
  }

  private Map<String, String> createHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    return headers;
  }
}
```



Taken help from this to call rest apis: https://www.appsloveworld.com/free-online-sample-rest-api-url-for-testing/#createtourist