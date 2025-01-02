
[![Android Build](https://github.com/jawnpaul/gpt-investor-android/actions/workflows/android_build.yml/badge.svg)](https://github.com/jawnpaul/gpt-investor-android/actions/workflows/android_build.yml)

# gpt-investor-android
## _Leveraging the power of AI to empower your financial decision-making!_
GPT Investor is an AI-powered mobile application that generates investment insights by synthesizing financial data from various sources across the internet, including Yahoo Finance, financial publications, and more. The AI model analyzes this data and provides users with valuable insights to help them make informed decisions when choosing stocks to invest in.

The application consumes data from [yfinance](https://github.com/ranaroussi/yfinance)

## Table of Contents

- [Architecture](#architecture)
- [Libraries](#libraries)
- [Process](#process)
- [Testing](#testing)
- [Organisation](#organisation)
- [Extras](#extras)
- [Demo](#demo)
- [Disclaimer](#disclaimer)

## Architecture


The Application is split into a three layer architecture:

- Data
- Domain
- Presentation

#### Data

The data layer handles the business logic and provides data from the API and Gemini. This layer uses the Repository pattern to fetch data from various data sources - sometimes this is calling the API to get data about specific companies, other times it is using gemini to provide information.

#### Domain

The domain layer contains the application specifics logic. It contains use cases that expose the actions that can be performed in the application.

The UseCases use a ```BaseUseCase``` interface that defines the parameters its taking in and output and also handles running the UseCases in a background thread leveraging Kotlin Coroutines.


#### Presentation

I used the MVVM pattern for the presentation layer. The Model essentially exposes the various states the view can be in. The ViewModel handles the UI logic and provides data via StateFlow to the view. The ViewModel talks to the domain layer with the individual use cases. Jetpack Compose is used to create the UI.

## Libraries

Libraries used in the application are:

- [Jetpack](https://developer.android.com/jetpack)
  - [Viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Manage UI related data in a lifecycle conscious way and act as a channel between use cases and UI.
  - [Compose](https://developer.android.com/jetpack/androidx/releases/compose) - Define your UI programmatically with composable functions that describe its shape and data dependencies.
  - [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation) - Build and structure your in-app UI, handle deep links, and navigate between screens.
  - [Room](https://developer.android.com/jetpack/androidx/releases/room) - Create, store, and manage persistent data backed by a SQLite database.
- [Retrofit](https://square.github.io/retrofit/) - Type safe http client and supports coroutines out of the box.
- [Moshi](https://github.com/square/moshi) - JSON Parser, used to parse requests on the data layer for Entities and understands Kotlin non-nullable
and default parameters.
- [okhttp-logging-interceptor](https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md) - Logs HTTP request and response data.
- [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow#stateflow) - A state-holder observable flow that emits the current and new state updates to its collectors.
- [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Library Support for coroutines. I used this for asynchronous programming in order to obtain data from the network as well as the database.
- [Coil](https://coil-kt.github.io/coil/) - This was used for loading images in the application.
- [Gemini](https://github.com/google-gemini/generative-ai-android) - Google AI client SDK for Android enables developers to use Google's generative AI models (like Gemini) to build AI-powered features and applications.
- [Timber](https://github.com/JakeWharton/timber) - A logger with a small, extensible API which provides utility on top of Android's normal Log class.
- [Compose-richtext](https://github.com/halilozercan/compose-richtext) - A collection of Compose libraries for advanced text formatting and alternative display types.
- [TimeAgo](https://github.com/marlonlom/timeago) - Simple java library for displaying dates as relative time ago language.
- [JUnit](https://junit.org/junit4/) - This was used for unit testing the repository, the use cases and the ViewModels.
- [Mockk](https://mockk.io/) This is a mocking library for Kotlin. I used it to provide test doubles during testing.
- [Truth](https://truth.dev/) - Assertions Library, provides readability as far as assertions are concerned.
- [Hilt](https://github.com/InsertKoinIO/koin) - Dependency injection plays a central role in the architectural pattern used.
For this reason I have chosen Hilt which is built on top of the battle tested DI framework - Dagger 2.

## Process

In general, any particular flow can be said to follow the steps below:
- The view sends an action to the ViewModel
- The ViewModel reaches out to the UseCase
- The UseCase via an abstraction layer reaches out to the repository
- The repository decides where to get the data from and returns (mapped to domain representation) either a success or a failure via a Sealed Either class.
- The UseCase gets the returned value and hand it over to the ViewModel
- The ViewModel maps the returned value to the presentation object.
- Finally, the ViewModel creates a view to model the state of the view then hands it over the composable.


## Testing

The different layer has various test cases.

The data layer has tests for the repository. To test the repository, test doubles were provided for the API service.

The UseCases were also tested by ensuring the UseCases called the right repository methods.

The presentation layer also has unit tests for the viewmodels.

## Organisation
I decided to organize my code based on features. Since many developers are expected to work on the project, developers can easily spot the folder to work on based on feature. This can also potentially reduce merge conflicts. It also makes it easy for new developers to come on board and if we want, we can easily have developers dedicated to different features of the application.

## Extras
The project uses ktlint to enforce proper code style. Github actions handles continous integration, and runs ktlint and unit tests.


## Installation

Minimum Api Level: 24

compileSdkVersion: 34

Build System: [Gradle](https://gradle.org/)

1. Get a free Gemini API key at [https://aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)
2. Get access token at [here](https://m7mxrc35hp.us-west-2.awsapprunner.com/get-token)
3. Create a project on firebase and add the `google-services.json` file in the app folder. See [here](https://firebase.google.com/docs/android/setup) for further instructions.
4. Clone the repo
   ```sh
   git clone https://github.com/jawnpaul/gpt-investor-android.git
   ```
5. Enter your keys in `local.properties`
   ```sh
   GEMINI_API_KEY=<Enter your GEMINI API KEY>
   GEMINI_DEBUG_KEY=<Enter your GEMINI API KEY>
   BASE_URL=https://m7mxrc35hp.us-west-2.awsapprunner.com/api/v1/
   ACCESS_TOKEN=<Enter your access token>
    ```
6. Create a `keystore.properties` file and add the following.
    ```sh
   KEY_ALIAS=abcd
   KEY_PASSWORD=abcd
   KEY_STORE_PASSWORD=abcd
   STORE_FILE=/user/abcd/
    ```
    **N.B**: Replace the values in the `keystore.properties` file with the correct values if you plan to release on Google playstore. Further instructions can be found [here](https://developer.android.com/studio/publish/app-signing)   

## Demo
1. Download on Google Playstore [here](https://play.google.com/store/apps/details?id=com.thejawnpaul.gptinvestor&pcampaignid=web_share)
2. Join internal test [here](https://forms.gle/8yZcWdjP1bXqB5pH8)

Find below screenshots of the application

|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/39986ffd-92ae-4ec3-b2f1-0ab50f2a089c" width=200/>|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/ce485437-735d-4aaa-ad9e-79d411a667ca" width=200/>|
|:----:|:----:|

|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/4a55a377-2328-4d14-bb31-1c97e04b313b" width=200/>|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/228ef994-5546-4ccf-99c7-e9b15be00f7a" width=200/>|
|:----:|:----:|

|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/fc58cf05-d4e1-4ac6-91f3-0024b43fb824" width=200/>|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/b9751290-3b93-419d-8898-fa4a3a600701" width=200/>|
|:----:|:----:|

|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/d944860a-49d4-4e5b-a2e5-a955b4d70a16" width=200/>|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/ab322ab7-9ece-4065-b8c2-b05f709e8b21" width=200/>|
|:----:|:----:|

|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/56057463-4f09-435a-84d0-e6c826c8006d" width=200/>|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/4bc9a5e5-6fa9-4d3f-8c03-7513735d1f05" width=200/>|
|:----:|:----:|

|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/b59e1288-f578-42e2-8e2c-3a270c9e68fb" width=200/>|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/38237d33-2b19-4124-9eeb-14acdbdffbe7" width=200/>|
|:----:|:----:|

|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/c0c5bd94-8927-4f0c-8f01-565dc08c4828" width=200/>|<img src="https://github.com/jawnpaul/gpt-investor-android/assets/29982834/934536d5-970b-4048-99f8-94680b4eb7ee" width=200/>|
|:----:|:----:|

## Disclaimer
GPT Investor is an educational and informational tool designed to assist in investment analysis. It should not be considered as financial advice or a substitute for professional investment guidance. Always conduct thorough research and consult with a qualified financial advisor before making any investment decisions. 

## License

MIT

**Free Software, Hell Yeah!**
