package com.example.controller

import com.example.constants.response
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.views.ViewsRenderer

var errorList  = mutableListOf<String>()
@Controller("/notFound")
class NotFoundController {
    private var viewsRenderer: ViewsRenderer<*>? = null

    fun NotFoundController(viewsRenderer: ViewsRenderer<*>?) {
        this.viewsRenderer = viewsRenderer
    }

    @Error(status = HttpStatus.NOT_FOUND, global = true)
    fun notFound(request: HttpRequest<*>): HttpResponse<*>? {
        errorList.clear()
        if (request.headers
                .accept()
                .stream()
                .anyMatch { mediaType -> mediaType.name.contains(MediaType.TEXT_HTML) }
        ) {
            println(request.headers.accept())
            return HttpResponse.ok(viewsRenderer!!.render("notFound", emptyMap<Any, Any>() as Nothing?, request))
                .contentType(MediaType.TEXT_HTML)
        }
        errorList.add("Page not found")
        response["errors"] = errorList

        return HttpResponse.ok(response)
    }
}