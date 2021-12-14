package com.tool.craft.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "config")
@Data
public class CraftConfig {
    private Map<String, List<String>> contas;
}
