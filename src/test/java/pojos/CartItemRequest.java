package pojos;

import java.util.Set;

/** POST /carts/current/items istek gövdesi. */
public class CartItemRequest {

    private Long productId;
    private Integer quantity;
    private Set<Long> additionIds;

    public CartItemRequest() {
    }

    public CartItemRequest(Long productId, Integer quantity, Set<Long> additionIds) {
        this.productId = productId;
        this.quantity = quantity;
        this.additionIds = additionIds;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Set<Long> getAdditionIds() {
        return additionIds;
    }

    public void setAdditionIds(Set<Long> additionIds) {
        this.additionIds = additionIds;
    }
}
