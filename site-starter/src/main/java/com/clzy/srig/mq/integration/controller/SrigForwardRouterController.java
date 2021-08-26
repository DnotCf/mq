package com.clzy.srig.mq.integration.controller;


import com.alibaba.fastjson.JSONObject;
import com.clzy.geo.core.common.dto.common.JsonResponse;
import com.clzy.geo.core.common.persistence.Page;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.enums.MQStuats;
import com.clzy.srig.mq.integration.service.ForwardRouterService;

import com.clzy.srig.mq.integration.service.ForwardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

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
public class SrigForwardRouterController extends BaseController {

    @Autowired
    private ForwardRouterService service;
    @Autowired
    private ForwardService forwardService;

    @ApiOperation(value = "自定义分页条件排序获取信息", notes = "自定义分页条件排序获取信息")
    @PostMapping("page")
    public JsonResponse list(@RequestBody  ForwardRouter entity, HttpServletRequest request, HttpServletResponse response) {
        Page<ForwardRouter> page = new Page<>(request, response);
        entity.setSourceType(1);
        page = service.findPage(page, entity);
        return JsonResponse.success(page);
    }

    @ApiOperation(value = "上传信息", notes = "上传信息")
    @PostMapping("save")
    public JsonResponse save(@RequestBody ForwardRouter entiy) {
        String check = check(entiy.getToServer());
        if (StringUtils.isNotBlank(check)) {
            check = "映射目标：" + check;
            return JsonResponse.error(-1, check, check);
        }
        String from = check(entiy.getFromServer());
        if (StringUtils.isNotBlank(from)) {
            from = "源数据：" + from;
            return JsonResponse.error(-1, from, from);
        }
        if (!MQIntegration.ServerType.HTTP.equals(entiy.getToServer().getType())) {
            if (StringUtils.isBlank(entiy.getFromTopic()) || StringUtils.isBlank(entiy.getToTopic())) {
                return JsonResponse.error(-1,"源fromTopic或目标toTopic不为空","源fromTopic或目标toTopic不为空");
            }
        }
        if (entiy.getExpireTime() == null) {
            return JsonResponse.error(-1, "过期时间不为空", "过期时间不为空");
        }
        String id = entiy.getId();
        service.save(entiy);
        if (StringUtils.isBlank(id)) {
            forwardService.addRouterTable(entiy);
        } else {
            forwardService.updateRouterTable(entiy);
        }
        entiy.setStatus(MQStuats.online.getCode());
        service.updateStatus(entiy);
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

    @ApiOperation(value = "停止所有消费", notes = "停止所有消费")
    @GetMapping("stopAll")
    public JsonResponse stopAllConnection() {
        List<ForwardRouter> list = service.findList(null);
        for (ForwardRouter router : list) {
            forwardService.deleteRouterTable(router);
        }
        forwardService.clearRouterTable();
        for (ForwardRouter router : list) {
            if (!MQStuats.offline.getCode().equals(router.getStatus())) {
                router.setExpireTime(null);
                router.setStatus(MQStuats.offline.getCode());
                service.updateStatus(router);
            }
        }
        return JsonResponse.success(list.size());
    }

    @ApiOperation(value = "启动所有消费", notes = "启动所有消费")
    @GetMapping("startAll")
    public JsonResponse startAllConnection() {
        List<ForwardRouter> list = service.findList(null);
        long count=0;
        for (ForwardRouter router : list) {
            if (router.getExpireTime() == null || router.getExpireTime().compareTo(new Date()) >= 0) {
                forwardService.addRouterTable(router);
                router.setExpireTime(null);
                router.setStatus(MQStuats.online.getCode());
                service.updateStatus(router);
                count++;
            }
        }
        return JsonResponse.success(count);
    }

    @ApiOperation(value = "停止消费", notes = "停止消费")
    @GetMapping("stop")
    public JsonResponse stopConnection(String id) {
        forwardService.deleteRouterTable(service.get(id));
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "启动映射消费", notes = "启动映射消费")
    @GetMapping("start")
    public JsonResponse startConnection(String id) {
        ForwardRouter router = service.get(id);
        if (router == null) {
            return JsonResponse.error(-1, "请先绑定映射", "请先绑定映射");
        }
        if (router.getExpireTime() != null && router.getExpireTime().compareTo(new Date()) < 0) {
            return JsonResponse.error(-1, "映射已经过期", "映射已经过期");
        }
        forwardService.addRouterTable(router);
        router.setExpireTime(null);
        router.setStatus(MQStuats.online.getCode());
        service.updateStatus(router);
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
        if (StringUtils.isNotBlank(entiy.getDefaultParam())) {
            try {
                JSONObject object = JSONObject.parseObject(entiy.getDefaultParam());
                Boolean noCheck = object.getBoolean("check");
                if (noCheck != null && noCheck == false) {
                    return JsonResponse.success(true);
                }
            } catch (Exception e) {
            }
        }
        String check = check(entiy);
        if (StringUtils.isNotBlank(check)) {
            return JsonResponse.error(-1, check, check);
        }
        ForwardRouter router = new ForwardRouter();
        router.setToServer(entiy);
        router.setToTopic(entiy.getTopic());
        return JsonResponse.success(forwardService.testConnection(router));
    }
}
