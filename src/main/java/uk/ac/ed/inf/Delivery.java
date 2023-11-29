package uk.ac.ed.inf;

/**
 * Delivery includes:
    - orderNo — the eight-character hexadecimal string assigned to this order in the orders REST service endpoint;
    - orderStatus — the OrderStatus value for this order, as a string;
    - orderValidationCode — the OrderValidationCode value for this order, as a string
    - costInPence — the total cost of the order as an integer, including the standard £1 delivery
      charge (be aware that this is a constant which might change).
    - useful class for writing to delivery.json using object mapper
 */

public class Delivery {

    public String orderNo;
    public String orderStatus;
    public String orderValidationCode;
    public int costInPence;

    public Delivery(String orderNo, String orderStatus, String orderValidationCode, int costInPence) {
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
        this.costInPence = costInPence;
    }
}
