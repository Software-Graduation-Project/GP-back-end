package gp.riham_aisha.back_end.controller;

import gp.riham_aisha.back_end.model.order.Order;
import gp.riham_aisha.back_end.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("")
    public ResponseEntity<List<Order>> getUserOrders() {
        return ResponseEntity.ok(orderService.getUserOrders());
    }

    @PostMapping("")
    public ResponseEntity<Void> placeOrder(@RequestBody Order order) {
        orderService.addOrder(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/store/{id}")
    public ResponseEntity<List<Order>> getStoreOrders(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getStoreOrders(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrder(@PathVariable Long id, String status) {
        orderService.updateOrder(id, status);
        return ResponseEntity.ok().build();
    }
}
