package com.clzy.srig.mq.integration.controller;

import com.clzy.geo.core.common.dto.common.JsonResponse;
import com.clzy.geo.core.utils.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: tangs
 * @Date: 2021/8/20 17:13
 */
@RestController("/api/login")
public class LoginController {

    @PostMapping
    public JsonResponse login(String username, String password, HttpServletRequest request) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return JsonResponse.success(false);
        }
        if (password.equals("123456ABc")) {
            request.getSession().setAttribute("user", "success");
            return JsonResponse.success(true);
        }
        return JsonResponse.success(false);
    }
}
