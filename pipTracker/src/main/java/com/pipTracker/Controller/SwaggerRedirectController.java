package com.pipTracker.Controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@CrossOrigin("*")
public class SwaggerRedirectController {

    @GetMapping("/v3/portal/ess/home")
    public void redirectToSwagger(HttpServletResponse response) throws IOException {

        // Redirect to exact Swagger UI URL with configUrl and #/ fragment
        // Browser will show: /swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/

        response.sendRedirect("/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config");
    }
}