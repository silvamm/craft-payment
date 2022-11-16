package com.tool.craft.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.tool.craft.dto.Ec2InfoLambdaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;


@Configuration
public class DataSourceConfig {

    @Value(value = "${username-db}")
    private String username;
    @Value("${password-db}")
    private String password;
    @Value("${lambda-url}")
    private String awsLambdaUrl;
    @Value("${database-id}")
    private String databaseInstanceId;

    @Bean
    public DataSource getDataSource() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Ec2InfoLambdaDto> response =
                restTemplate.exchange(awsLambdaUrl + "/" + databaseInstanceId,
                        HttpMethod.GET,
                        null,
                        Ec2InfoLambdaDto.class);

        Ec2InfoLambdaDto ec2Info = response.getBody();
        if(ec2Info == null)
            throw new IllegalStateException("Não foi possivel obter informações do banco de dados");
        if(ec2Info.getPublicDnsName() == null)
            throw new IllegalStateException("Banco de dados indisponivel");

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL("jdbc:mysql://"+ec2Info.getPublicDnsName()+"/craft_db");
        mysqlDataSource.setUser(username);
        mysqlDataSource.setPassword(password);
        return mysqlDataSource;
    }
}
