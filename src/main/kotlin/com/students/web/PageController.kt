package com.students.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PageController(
    @Value("\${app.api.base:/api}")
    private val apiBase: String
) {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("apiBase", apiBase)
        return "index"
    }
}
