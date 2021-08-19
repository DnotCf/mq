package com.clzy.srig.mq.integration.controller;


import com.clzy.geo.core.common.dto.common.JsonResponse;
import com.clzy.geo.core.common.persistence.Page;
import com.clzy.geo.core.utils.StringUtils;
import com.clzy.srig.mq.integration.entity.ForwardRouter;
import com.clzy.srig.mq.integration.entity.MQServer;
import com.clzy.srig.mq.integration.enums.MQIntegration;
import com.clzy.srig.mq.integration.service.ForwardService;
import com.clzy.srig.mq.integration.service.MQServerService;
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
@RequestMapping("/api/server")
@Api(tags = "数据源相关的接口")
public class SrigMqServerController {

    @Autowired
    private MQServerService service;
    @Autowired
    private ForwardService forwardService;

    @ApiOperation(value = "自定义分页条件排序获取信息", notes = "自定义分页条件排序获取信息")
    @PostMapping("page")
    public JsonResponse list(@RequestBody @ApiParam MQServer entiy, HttpServletRequest request, HttpServletResponse response) {
        Page<MQServer> page = new Page<>(request, response);
        page = service.findPage(page, entiy);
        return JsonResponse.success(page);
    }

    @ApiOperation(value = "上传信息", notes = "上传信息")
    @PostMapping("save")
    public JsonResponse save(@RequestBody MQServer entiy) {
        String check = check(entiy);
        if (StringUtils.isNotBlank(check)) {
            return JsonResponse.success(check);
        }
        service.save(entiy);
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
            service.delete(new MQServer(id));
        }
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "连接测试", notes = "连接测试")
    @PostMapping("testConnection")
    public JsonResponse testConnection(@RequestBody MQServer entiy) {
        ForwardRouter router = new ForwardRouter();
        router.setFromServer(entiy);
        router.setFromTopic(entiy.getTopic());
        router.setToServer(null);
        return JsonResponse.success(forwardService.testConnection(router));
    }

    public String check(MQServer entity) {
        String msg = null;
        if (entity.getType() == null) {
            return "数据源类型不为空";
        }
        if (StringUtils.isBlank(entity.getName())) {
            return "数据源名称不为空";
        }
        if (StringUtils.isBlank(entity.getTopic())) {
            return "topic不为空";
        }
        if (MQIntegration.ServerType.Aliyun_RocketMQ.equals(entity.getType())) {
            if (StringUtils.isBlank(entity.getCluster())) {
                return "服务地址不为空";
            }
            if (StringUtils.isBlank(entity.getSecretKey()) || StringUtils.isBlank(entity.getAccessKey())) {
                return "阿里云授权：secretKey、accessKey不为空";
            }
            if (StringUtils.isBlank(entity.getGroup())) {
                return "groupId组不为空";
            }
            if (StringUtils.isBlank(entity.getTag())) {
                return "tag不为空";
            }
        }else if (MQIntegration.ServerType.RocketMQ.equals(entity.getType())){
            if (StringUtils.isBlank(entity.getCluster()) && (StringUtils.isBlank(entity.getIp()) || null == entity.getPort())) {
                return "服务地址或ip+端口不为空";
            }

        }else if (MQIntegration.ServerType.MQTT.equals(entity.getType())){
            if (StringUtils.isBlank(entity.getProtocol())) {
                return "传输协议不为空";
            }
            if (StringUtils.isBlank(entity.getIp())) {
                return "ip地址不为空";
            }
            if (null == entity.getPort()) {
                return "端口不为空";
            }
            if (StringUtils.isBlank(entity.getClientName())) {
                return "客户端名称不为空";
            }
        }else if (MQIntegration.ServerType.AMQP.equals(entity.getType())){
//            if (StringUtils.isBlank(entity.getProtocol())) {
//                return "传输协议不为空";
//            }
            if (StringUtils.isBlank(entity.getIp())) {
                return "ip地址不为空";
            }
            if (null == entity.getPort()) {
                return "端口不为空";
            }
            if (StringUtils.isBlank(entity.getUsername()) || StringUtils.isBlank(entity.getPassword())) {
                return "用户名密码不为空";
            }
            if (StringUtils.isBlank(entity.getClientName())) {
                return "客户端名称不为空";
            }
        }else if (MQIntegration.ServerType.HTTP.equals(entity.getType())){

        }else {
            return "没有匹配的数据源类型：MQTT, AMQP, Aliyun_RocketMQ, RocketMQ, HTTP";
        }
        return msg;
    }
}
