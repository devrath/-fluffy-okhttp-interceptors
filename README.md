# fluffy-okhttp-interceptors
<p align="center">
<a><img src="https://github.com/devrath/fluffy-okhttp-interceptors/blob/main/assets/Banner.png"></a>
</p>


<p align="center">
<a><img src="https://img.shields.io/badge/Built%20Using-Kotlin-silver?style=for-the-badge&logo=kotlin"></a>
<a><img src="https://img.shields.io/badge/Built%20By-Android%20Studio-red?style=for-the-badge&logo=android%20studio"></a>  
<a><img src="https://img.shields.io/badge/Tool-OkHttp-green?style=for-the-badge&logo=tools"></a>  
<a><img src="https://img.shields.io/badge/OkHttp%20Interceptors-purple?style=for-the-badge&logo=tools"></a>  
</p>

---

<div align="center">
  
| Topics |
| ------ |
| [```What is OkHttp Interceptor```](https://github.com/devrath/fluffy-okhttp-interceptors/wiki/What-is-OkHttp-Interceptor) |
| [```Types of Interceptors```](https://github.com/devrath/fluffy-okhttp-interceptors/wiki/Types-of-Interceptors) |
| [```HttpLoggingInterceptor```](https://github.com/devrath/fluffy-okhttp-interceptors/wiki/HttpLoggingInterceptor) |
| [```What is Analytics Interceptor```](https://github.com/devrath/fluffy-okhttp-interceptors/wiki/What-is-Analytics-Interceptor) |
| [```What is ApiKey or tolken Interceptor```](https://github.com/devrath/fluffy-okhttp-interceptors/wiki/What-is-ApiKey-or-tolken-Interceptor) |
  
</div>


<div align="center">

| `Representation` | **`𝙾𝚞𝚝𝚙𝚞𝚝`** |
| ----------- | ----------- |
| <img src="https://github.com/devrath/fluffy-okhttp-interceptors/blob/main/assets/okhttp.png" width="340" height="300"/> | <img src="https://github.com/devrath/fluffy-okhttp-interceptors/blob/main/assets/demo.gif" width="220" height="460"/> |

</div>

## `What is OkHttp Interceptor`
* OkHttp is a mechanism that helps you `monitor` and re-write network calls.
* In the image below we can see that a call is sent from the application **`->`** Then it is received by the interceptor and modified **`->`** Then further modified call is received at the server.
<p align="center">
  <img src="https://github.com/devrath/fluffy-okhttp-interceptors/blob/main/assets/interceptor1.png">
</p>

## Chaining OkHttp interceptor
* We can chain multiple interceptors and modify the request
* Order of the chaining is important
* Okhttp keeps a list of interceptors and processes them in the same order in which they are added
<p align="center">
  <img src="https://github.com/devrath/fluffy-okhttp-interceptors/blob/main/assets/interceptor2.png">
</p>

## What are the different types of Interceptors
There are two types of interceptors 
* Application Interceptors
* Network Interceptors
<p align="center">
  <img src="https://github.com/devrath/fluffy-okhttp-interceptors/blob/main/assets/interceptorstypes.png">
</p>

| **`Application Interceptors`** | **`Network Interceptors`** |
| -- | -- |
| The application interceptors are the type of interceptors that are found between the application and the `okhttp module`. | The network interceptors are the type of interceptors that are found between the `okhttp module` and `remote server`.|
| They are not concerned with the intermediate responses and focus on the final response sent to the application. | They are concerned with the intermediary responses from the time of application making the call and receiving the final response. |

## What is an Analytics Interceptor
* Even here we customize the regular interceptor to send certain specific user data to the server on each API request 

### Use case 
* Suppose again if we are sending information like `Device-ID`, `OS-version` etc to the server to understand the customer who is using the API service.
* Now again instead of sending these details which can be of any length, we can send it from one place having a common interceptor

### Example 
```kotlin
class AnalyticsInterceptor(private val context: Context): Interceptor {

    private val APP_VERSION = "X-App-Version"
    private val DEVICE_MODEL = "X-Device-Model"
    private val DEVICE_PLATFORM = "X-Device-Platform"
    private val OS_VERSION = "X-Device-OS-Version"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val requestBuilder: Request.Builder = request.newBuilder()

        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version = packageInfo.versionName

        requestBuilder.addHeader(APP_VERSION, version)
        requestBuilder.addHeader(OS_VERSION, Build.VERSION.SDK_INT.toString())
        requestBuilder.addHeader(DEVICE_MODEL, Build.MODEL)
        requestBuilder.addHeader(DEVICE_PLATFORM, "android")

        return chain.proceed(requestBuilder.build())
    }
}
```

## What is an API key Interceptor
* There is no such special thing as `ApiKey` or `tolken` Interceptor.
* We customize the interceptor in such a way 

### Use case 
* Suppose in every API request we want to pass an auth token to the server in the header.
* We need not have to pass while creating every API request.
* Instead of it we can pass it in a custom interceptor 

### Example 
```kotlin
class ApiKeyInterceptor: Interceptor {

    private val apiKeyQueryParameterKey = "api_key"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val url = originalUrl.newBuilder()
            .addQueryParameter(apiKeyQueryParameterKey, BuildConfig.THE_MOVIE_DB_API_TOKEN)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(url)
            .build()

        return chain.proceed(newRequest)
    }
}
```
* Add the object while creating the `okhttp` request 
```kotlin
val okHttpClient = OkHttpClient.Builder()
.readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
.connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
.addInterceptor(ApiKeyInterceptor()) 
```

## What is an `HTTP Logging Interceptor`
* The HTTP Logging Interceptor is an interceptor that helps to log all the HTTP requests that are being sent to the server. 
* It also can log all the responses that are sent from the server to the application.

### How useful is HTTP logging interceptor
* It is helpful in debugging the application on network-related issues when building the application.

### What is `RedactHeader` in HTTP logging interceptor. 
* This is the ability to remove certain information from logging in to the terminal.
* For example, we can remove the API key getting logged if it is sent in a request to the server.

### Add the entry in gradle
```gradle
implementation "com.squareup.okhttp3:logging-interceptor:4.9.0"
```

### Example 

* Add the code 
```kotlin
 val loggingInterceptor = HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
      loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
      loggingInterceptor.redactHeader("x-amz-cf-id")
```

* And add it while building the `okhttp` client
```kotlin
object OkHttpProvider {

  // Timeout for the network requests
  private const val REQUEST_TIMEOUT = 3L

  private var okHttpClient: OkHttpClient? = null

  fun getOkHttpClient(context: Context): OkHttpClient {
    return if (okHttpClient == null) {
      val loggingInterceptor = HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
      loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
      loggingInterceptor.redactHeader("x-amz-cf-id")

      val okHttpClient = OkHttpClient.Builder()
          .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
          .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
          .addInterceptor(loggingInterceptor)
          .build()
      OkHttpProvider.okHttpClient = okHttpClient
      okHttpClient
    } else {
      okHttpClient!!
    }
  }
}
```


## **`𝚂𝚞𝚙𝚙𝚘𝚛𝚝`** ☕
If you feel like support me a coffee for my efforts, I would greatly appreciate it.</br>
<a href="https://www.buymeacoffee.com/devrath" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/yellow_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

## **`𝙲𝚘𝚗𝚝𝚛𝚒𝚋𝚞𝚝𝚎`** 🙋‍♂️
Read [contribution guidelines](CONTRIBUTING.md) for more information regarding contribution.

## **`𝙵𝚎𝚎𝚍𝚋𝚊𝚌𝚔`** ✍️ 
Feature requests are always welcome, [File an issue here](https://github.com/devrath/fluffy-okhttp-interceptors/issues/new).

## **`𝙵𝚒𝚗𝚍 𝚝𝚑𝚒𝚜 𝚙𝚛𝚘𝚓𝚎𝚌𝚝 𝚞𝚜𝚎𝚏𝚞𝚕`** ? ❤️
Support it by clicking the ⭐ button on the upper right of this page. ✌️

## **`𝙻𝚒𝚌𝚎𝚗𝚜𝚎`** ![Licence](https://img.shields.io/github/license/google/docsy) :credit_card:
This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/devrath/android-congenial-fortnight-darktheme/blob/main/LICENSE) file for details


<p align="center">
<a><img src="https://forthebadge.com/images/badges/built-for-android.svg"></a>
</p>
