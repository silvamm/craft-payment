package com.tool.craft.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.tool.craft.dto.Ec2UrlLambdaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;


@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${app.aws.lambda.url}")
    private String awsLambdaUrl;

    @Bean
    public DataSource getDataSource() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Ec2UrlLambdaDto> response =
                restTemplate.exchange(awsLambdaUrl,
                        HttpMethod.GET,
                        null,
                        Ec2UrlLambdaDto.class);

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL("jdbc:mysql://"+response.getBody().getPublicDnsName()+"/craft_db");
        mysqlDataSource.setUser(user);
        mysqlDataSource.setPassword(password);
        return mysqlDataSource;
    }
}
