package com.tool.craft.dto;

import lombok.Data;

@Data
public class Ec2UrlLambdaDto {

    private String keyName;
    private Ec2StateDto state;
    private String publicDnsName;
    
}
