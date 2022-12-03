package com.tool.craft.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AppStatusRestController {

    @Value(value="${app.version}")
    private String appVersion;
    @Value(value="${app.build.timestamp}")
    private String appBuildTimestamp;

    @GetMapping("/live")
    public Map<String, String> live(){
        HashMap<String, String> map = new HashMap<>();
        map.put("status", "OK");
        map.put("version", appVersion);
        map.put("timestamp", appBuildTimestamp);
        return map;
    }

}
