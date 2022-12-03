package com.tool.craft.config;

import com.tool.craft.domain.enumm.BillType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "config")
public class CraftConfig {

    private Map<BillType, List<String>> bills;
    private List<String> targetLabels;


}
