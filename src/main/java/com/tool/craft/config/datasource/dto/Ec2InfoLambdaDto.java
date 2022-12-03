package com.tool.craft.config.datasource.dto;

import lombok.Data;

@Data
public class Ec2InfoLambdaDto {

    private String keyName;
    private Ec2StateDto state;
    private String publicDnsName;
    
}
