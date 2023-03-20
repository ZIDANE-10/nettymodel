package com.jian.nettyclient.controller;

import com.jian.nettyclient.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/base")
@RestController
public class BaseController {

    @Resource
    private BaseService baseService;

    @RequestMapping("/init")
    public Map<String ,Object> init(){
        Map<String, Object> result = new HashMap<>();
//        baseService.initData();
        return result;
    }

    @RequestMapping("/startNettyServer")
    public Map<String ,Object> startNettyServer(){
        Map<String, Object> result = new HashMap<>();
        result.put("result",baseService.startNettyServer());
        return result;
    }

    @RequestMapping("/shutdownNettyServer")
    public Map<String ,Object> shutdownNettyServer(){
        Map<String, Object> result = new HashMap<>();
        result.put("result",baseService.shutdownNettyServer());
        return result;
    }

    @RequestMapping("/startNettyClient")
    public Map<String ,Object> startNettyClient(){
        Map<String, Object> result = new HashMap<>();
        result.put("result",baseService.startNettyClient());
        return result;
    }

    @RequestMapping("/send")
    public Map<String ,Object> send(){
        Map<String, Object> result = new HashMap<>();
        result.put("result",baseService.send());
        return result;
    }

    @RequestMapping("/pause")
    public Map<String ,Object> pause(){
        Map<String, Object> result = new HashMap<>();
        result.put("result",baseService.pause());
        return result;
    }

    @RequestMapping("/startTps")
    public Map<String ,Object> startTps(){
        Map<String, Object> result = new HashMap<>();
        baseService.startTps();
        result.put("result",baseService.getTps());
        return result;
    }

    @RequestMapping("/getTps")
    public Map<String ,Object> getTps(){
        Map<String, Object> result = new HashMap<>();
        result.put("result",baseService.getTps());
        return result;
    }
}
