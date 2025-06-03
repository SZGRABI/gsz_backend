package cz.rohlik.gsz.controller;

import cz.rohlik.gsz.dto.OrderDTO;
import cz.rohlik.gsz.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create an order")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO order) {
        OrderDTO newOrder = orderService.createOrder(order);
        return ResponseEntity.ok(newOrder);
    }

    @Operation(summary = "Cancel an order")
    @PutMapping({"/{id}/cancel"})
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable("id") Long id) {
        OrderDTO cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

    @Operation(summary = "Pay an order")
    @PutMapping({"/{id}/pay"})
    public ResponseEntity<OrderDTO> payOrder(@PathVariable("id") Long id) {
        OrderDTO paidOrder = orderService.payOrder(id);
        return ResponseEntity.ok(paidOrder);
    }
}
