package com.thejawnpaul.gptinvestor.utils

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.HttpURLConnection

class RequestDispatcher {

    /**
     * Return ok response from mock server
     */
    inner class RequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse = when (request.path) {
            COMPANIES_REQUEST_PATH -> {
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody(getJson("response/companies.json"))
            }

            COMPANY_REQUEST_PATH -> {
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody(getJson("response/company.json"))
            }

            SAVE_COMPARISON_REQUEST_PATH -> {
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody(getJson("response/default_save.json"))
            }

            else -> throw IllegalArgumentException("Unknown Request Path ${request.path}")
        }
    }

    /**
     * Return conflict response from mock server
     */
    internal inner class ConflictRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) = MockResponse().setResponseCode(HttpURLConnection.HTTP_CONFLICT)
    }

    /**
     * Return bad request response from mock server
     */
    internal inner class BadRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) = MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
    }

    /**
     * Return server error response from mock server
     */
    internal inner class ErrorRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) = MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
    }

    /**
     * Return incorrect body(aka null) response from mock server
     */
    internal inner class IncorrectBodyRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) = MockResponse().setResponseCode(HttpURLConnection.HTTP_NO_CONTENT)
    }

    /**
     * Return unauthorized error response from mock server
     */
    internal inner class UnAuthorizedErrorRequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) = MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
    }
}
