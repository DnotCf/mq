package com.clzy.srig.mq.integration.controller;


import com.clzy.geo.core.common.dto.common.JsonResponse;
import com.clzy.geo.core.common.persistence.Page;
import com.clzy.srig.mq.integration.entity.MQServer;
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
 *  前端控制器
 * </p>
 *
 * @author tangs
 * @since 2021-08-17
 */
@RestController
@RequestMapping("/server")
@Api(tags = "数据源相关的接口")
public class SrigMqServerController {

    @Autowired
    private MQServerService service;


    @ApiOperation(value = "自定义分页条件排序获取信息",notes = "自定义分页条件排序获取信息")
    @PostMapping("page")
    public JsonResponse list(@RequestBody @ApiParam MQServer entiy, HttpServletRequest request, HttpServletResponse response) {
        Page<MQServer> page = new Page<>(request, response);
        page = service.findPage(page, entiy);
        return JsonResponse.success(page);
    }

    @ApiOperation(value = "上传信息",notes = "上传信息")
    @PostMapping("save")
    public JsonResponse save(@RequestBody MQServer entiy) {
        service.save(entiy);
        return JsonResponse.success(true);
    }

    @ApiOperation(value = "根据主键获取信息",notes = "根据主键获取信息")
    @GetMapping("get")
    public JsonResponse get(String id)
    {
    return JsonResponse.success(service.get(id));
    }

    @ApiOperation(value = "删除信息",notes = "删除信息")
    @PostMapping("delete")
    public JsonResponse delete(String ids)
    {
        String[] idsAry = ids.split(",");
        for (String id : idsAry) {
            service.delete(new MQServer(id));
        }
        return JsonResponse.success(true);
    }
}
