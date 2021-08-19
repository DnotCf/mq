package com.clzy.srig.mq.integration.controller;


import com.clzy.geo.core.common.dto.common.JsonResponse;
import com.clzy.geo.core.common.persistence.Page;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.service.ForwardRouterService;

import com.clzy.srig.mq.integration.service.ForwardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tangs
 * @since 2021-08-17
 */
@RestController
@RequestMapping("/api/router")
@Api(tags = "映射管理相关的接口")
public class SrigForwardRouterController {

    @Autowired
    private ForwardRouterService service;
    @Autowired
    private ForwardService forwardService;

    @ApiOperation(value = "自定义分页条件排序获取信息", notes = "自定义分页条件排序获取信息")
    @PostMapping("page")
    public JsonResponse list(@RequestBody @ApiParam ForwardRouter entiy, HttpServletRequest request, HttpServletResponse response) {
        Page<ForwardRouter> page = new Page<>(request, response);
        page = service.findPage(page, entiy);
        return JsonResponse.success(page);
    }

    @ApiOperation(value = "上传信息", notes = "上传信息")
    @PostMapping("save")
    public JsonResponse save(@RequestBody ForwardRouter entiy) {
        String id = entiy.getId();
        service.save(entiy);
        if (StringUtils.isBlank(id)) {
            forwardService.addRouterTable(entiy);
        } else {
            forwardService.updateRouterTable(entiy);
        }
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "根据主键获取信息", notes = "根据主键获取信息")
    @GetMapping("get")
    public JsonResponse get(String id) {
        return JsonResponse.success(service.get(id));
    }

    @ApiOperation(value = "删除信息", notes = "删除信息")
    @PostMapping("delete")
    public JsonResponse delete(String ids) {
        String[] idsAry = ids.split(",");
        for (String id : idsAry) {
            ForwardRouter router = service.get(id);
            if (router == null) {
                continue;
            }
            forwardService.deleteRouterTable(router);
            service.delete(new ForwardRouter(id));
        }
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "停止消费", notes = "停止消费")
    @GetMapping("stop")
    public JsonResponse stopConnection(String id) {
        forwardService.stopConsumer(service.get(id));
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "启动映射消费", notes = "启动映射消费")
    @GetMapping("start")
    public JsonResponse startConnection(String id) {
        forwardService.startConsumer(service.get(id));
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "更新过期时间", notes = "更新过期时间")
    @PostMapping("expireTime")
    public JsonResponse updateExpireTime(@RequestBody ForwardRouter entiy) {
        service.updateStatus(entiy);
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "连接测试", notes = "连接测试")
    @PostMapping("testConnection")
    public JsonResponse testConnection(@RequestBody MQServer entiy) {
        ForwardRouter router = new ForwardRouter();
        router.setToServer(entiy);
        router.setToTopic(entiy.getTopic());
        return JsonResponse.success(forwardService.testConnection(router));
    }
}
