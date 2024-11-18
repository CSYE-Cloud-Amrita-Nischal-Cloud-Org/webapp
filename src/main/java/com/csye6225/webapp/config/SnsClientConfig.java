package com.csye6225.webapp.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SnsClientConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access.key:}")
    private String accessKey;

    @Value("${aws.secret.key:}")
    private String secretKey;

    @Value("${localstack.endpoint:}")
    private String localstackEndpoint;

    @Bean
    @Profile("!local")
    public AmazonSNSAsync awsSnsClient() {
        return AmazonSNSAsyncClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
    }

    @Bean
    @Profile("local")
    public AmazonSNSAsync localstackSnsClient() {
        return AmazonSNSAsyncClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonSNSAsyncClientBuilder.EndpointConfiguration(localstackEndpoint, region)
                )
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)
                ))
                .build();
    }
}
