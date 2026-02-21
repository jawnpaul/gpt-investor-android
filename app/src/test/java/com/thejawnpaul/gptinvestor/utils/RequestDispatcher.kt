package com.thejawnpaul.gptinvestor.utils

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpStatusCode

class RequestDispatcher {

    val successHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = { request ->
        val path = request.url.encodedPath
        when {
            path.contains(COMPANIES_REQUEST_PATH) -> {
                mockResponse(getJson("response/companies.json"))
            }

            path.contains(COMPANY_REQUEST_PATH) -> {
                mockResponse(getJson("response/company.json"))
            }

            path.contains(SAVE_COMPARISON_REQUEST_PATH) -> {
                mockResponse(getJson("response/default_save.json"))
            }

            else -> throw IllegalArgumentException("Unknown Request Path $path")
        }
    }

    val conflictHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        mockResponse("", HttpStatusCode.Conflict)
    }

    val badRequestHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        mockResponse("", HttpStatusCode.BadRequest)
    }

    val errorHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        mockResponse("", HttpStatusCode.InternalServerError)
    }

    val noContentHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        mockResponse("", HttpStatusCode.NoContent)
    }

    val unauthorizedHandler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
        mockResponse("", HttpStatusCode.Unauthorized)
    }
}
