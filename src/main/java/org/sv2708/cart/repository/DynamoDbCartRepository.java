package org.sv2708.cart.repository;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.sv2708.cart.model.Cart;
import org.sv2708.cart.service.CartAlreadyExistsException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

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
    public void update(Cart cart) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(CartMapper.toItem(cart))
                .conditionExpression("attribute_exists(cart_id)")
                .build());
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
}
