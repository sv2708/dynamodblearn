package org.sv2708.cart.repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.sv2708.cart.api.UpdateCartRequest;
import org.sv2708.cart.model.Cart;
import org.sv2708.cart.service.CartAlreadyExistsException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@ApplicationScoped
public class DynamoDbCartRepository implements CartRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    DynamoDbCartRepository(
            DynamoDbClient dynamoDbClient,
            @ConfigProperty(name = "cart.table-name") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public void create(Cart cart) {
        try {
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(CartMapper.toItem(cart))
                    .conditionExpression("attribute_not_exists(cart_id)")
                    .build());
        } catch (ConditionalCheckFailedException e) {
            throw new CartAlreadyExistsException(cart.cartId(), e);
        }
    }

    @Override
    public List<Cart> list() {
        return dynamoDbClient.scanPaginator(ScanRequest.builder()
                        .tableName(tableName)
                        .build())
                .items()
                .stream()
                .map(CartMapper::fromItem)
                .toList();
    }

    @Override
    public Optional<Cart> find(String cartId) {
        var response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(CartMapper.key(cartId))
                .build());
        return Optional.ofNullable(CartMapper.fromItem(response.item()));
    }

    @Override
    public Optional<Cart> patch(String cartId, UpdateCartRequest request, Instant updatedAt) {
        var attributeNames = new HashMap<String, String>();
        var attributeValues = new HashMap<String, AttributeValue>();
        var setExpressions = new StringJoiner(", ", "SET ", "");

        addStringUpdate(attributeNames, attributeValues, setExpressions,
                "customer_id", request.customerId());
        addStringUpdate(attributeNames, attributeValues, setExpressions,
                "status", request.status());
        addStringUpdate(attributeNames, attributeValues, setExpressions,
                "currency", request.currency());

        if (request.items() != null) {
            attributeNames.put("#items", "items");
            attributeValues.put(":items", CartMapper.itemsValue(request.items()));
            setExpressions.add("#items = :items");

            attributeNames.put("#total_amount", "total_amount");
            attributeValues.put(":total_amount", CartMapper.numberValue(CartMapper.totalAmount(request.items())));
            setExpressions.add("#total_amount = :total_amount");
        }

        attributeNames.put("#updated_at", "updated_at");
        attributeValues.put(":updated_at", CartMapper.instantValue(updatedAt));
        setExpressions.add("#updated_at = :updated_at");

        try {
            var response = dynamoDbClient.updateItem(UpdateItemRequest.builder()
                    .tableName(tableName)
                    .key(CartMapper.key(cartId))
                    .updateExpression(setExpressions.toString())
                    .conditionExpression("attribute_exists(cart_id)")
                    .expressionAttributeNames(attributeNames)
                    .expressionAttributeValues(attributeValues)
                    .returnValues(ReturnValue.ALL_NEW)
                    .build());
            return Optional.ofNullable(CartMapper.fromItem(response.attributes()));
        } catch (ConditionalCheckFailedException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String cartId) {
        try {
            dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(CartMapper.key(cartId))
                    .conditionExpression("attribute_exists(cart_id)")
                    .build());
            return true;
        } catch (ConditionalCheckFailedException e) {
            return false;
        }
    }

    private static void addStringUpdate(
            HashMap<String, String> attributeNames,
            HashMap<String, AttributeValue> attributeValues,
            StringJoiner setExpressions,
            String attributeName,
            String value) {
        if (value == null) {
            return;
        }

        var namePlaceholder = "#" + attributeName;
        var valuePlaceholder = ":" + attributeName;
        attributeNames.put(namePlaceholder, attributeName);
        attributeValues.put(valuePlaceholder, CartMapper.stringValue(value));
        setExpressions.add(namePlaceholder + " = " + valuePlaceholder);
    }
}
