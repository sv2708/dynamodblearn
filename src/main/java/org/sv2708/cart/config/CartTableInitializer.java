package org.sv2708.cart.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.sv2708.cart.repository.CartMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@ApplicationScoped
public class CartTableInitializer {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final boolean createTableOnStartup;

    CartTableInitializer(
            DynamoDbClient dynamoDbClient,
            @ConfigProperty(name = "cart.table-name") String tableName,
            @ConfigProperty(name = "cart.create-table-on-startup", defaultValue = "true") boolean createTableOnStartup) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.createTableOnStartup = createTableOnStartup;
    }

    void createCartTableIfMissing(@Observes StartupEvent startupEvent) {
        if (!createTableOnStartup) {
            return;
        }

        try {
            dynamoDbClient.describeTable(builder -> builder.tableName(tableName));
        } catch (ResourceNotFoundException e) {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(CartMapper.CART_ID)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(CartMapper.CART_ID)
                            .keyType(KeyType.HASH)
                            .build())
                    .build());
        }
    }
}
