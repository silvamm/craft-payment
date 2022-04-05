package com.tool.craft.config.craft;

import com.tool.craft.config.craft.Target;
import com.tool.craft.enumm.BillType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Data
@Component
@ConfigurationProperties(prefix = "config")
public class CraftConfig {

    private Map<BillType, List<String>> bills;
    private List<Target> targets;

    public List<String> labelsToFind(){
        return targets.stream().map(Target::getLabel).collect(toList());
    }
}
