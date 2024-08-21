package com.softeer.backend.global.common.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * swagger 응답을 처리하는 컨트롤러
 */
@Controller
public class SwaggerController {

    @RequestMapping("/swagger")
    public String getRedirectUrl() {
        return "redirect:swagger-ui/index.html";
    }
}
