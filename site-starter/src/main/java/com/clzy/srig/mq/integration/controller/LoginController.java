package com.clzy.srig.mq.integration.controller;

import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.common.dto.common.JsonResponse;
import com.clzy.geo.core.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: tangs
 * @Date: 2021/8/20 17:13
 */
@RestController()
@RequestMapping("/api/")
@Api(tags = "登录相关的接口")
public class LoginController {

    @CrossOrigin
    @PostMapping("login")
    public JsonResponse login(@RequestBody JSONObject object, HttpServletRequest request) {
        String userName = object.getString("userName");
        String password = object.getString("password");
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            return JsonResponse.success(false);
        }
        if (password.equals("123456ABc")) {
            request.getSession().setAttribute("user", "success");
            return JsonResponse.success(true);
        }
        return JsonResponse.success(false);
    }
    @GetMapping("get_info")
    public JsonResponse getInfo() {
        return JsonResponse.success(true);
    }
}
