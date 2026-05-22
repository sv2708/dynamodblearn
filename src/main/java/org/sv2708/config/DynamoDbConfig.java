package org.sv2708.config;

import java.net.URI;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ApplicationScoped
public class DynamoDbConfig {

    @ConfigProperty(name = "aws.region", defaultValue = "us-east-1")
    String awsRegion;

    @ConfigProperty(name = "aws.dynamodb.endpoint-override")
    Optional<String> endpointOverride;

    @Produces
    @Singleton
    DynamoDbClient dynamoDbClient() {
        var builder = DynamoDbClient.builder()
                .region(Region.of(awsRegion));

        var localEndpoint = endpointOverride
                .map(String::trim)
                .filter(endpoint -> !endpoint.isBlank());

        localEndpoint
                .map(URI::create)
                .ifPresent(builder::endpointOverride);

        if (localEndpoint.isPresent()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("local", "local")));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.builder().build());
        }

        return builder.build();
    }

    void closeDynamoDbClient(@Disposes DynamoDbClient dynamoDbClient) {
        dynamoDbClient.close();
    }
}
