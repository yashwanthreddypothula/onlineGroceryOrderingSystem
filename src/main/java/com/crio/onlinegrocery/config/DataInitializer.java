package com.crio.onlinegrocery.config;

import com.crio.onlinegrocery.entity.Customer;
import com.crio.onlinegrocery.entity.GroceryItem;
import com.crio.onlinegrocery.entity.Order;
import com.crio.onlinegrocery.repository.CustomerRepository;
import com.crio.onlinegrocery.repository.GroceryItemRepository;
import com.crio.onlinegrocery.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CustomerRepository customerRepository;
    private final GroceryItemRepository groceryItemRepository;
    private final OrderRepository orderRepository;

    public DataInitializer(CustomerRepository customerRepository,
                           GroceryItemRepository groceryItemRepository,
                           OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.groceryItemRepository = groceryItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0 && groceryItemRepository.count() == 0) {
            log.info("Initializing sample data for Online Grocery Ordering System...");

            // 1. Create Customers
            Customer alice = customerRepository.save(new Customer(
                    "Alice Smith",
                    "alice.smith@example.com",
                    "123 Oak Street, Springfield",
                    "+1-555-0101"
            ));

            Customer bob = customerRepository.save(new Customer(
                    "Bob Johnson",
                    "bob.johnson@example.com",
                    "456 Elm Street, Metropolis",
                    "+1-555-0102"
            ));

            Customer charlie = customerRepository.save(new Customer(
                    "Charlie Brown",
                    "charlie.brown@example.com",
                    "789 Pine Avenue, Gotham",
                    "+1-555-0103"
            ));

            // 2. Create Grocery Items
            GroceryItem milk = groceryItemRepository.save(new GroceryItem("Whole Milk 1L", "Dairy", 3.49, 50));
            GroceryItem bread = groceryItemRepository.save(new GroceryItem("Whole Wheat Bread", "Bakery", 2.99, 40));
            GroceryItem eggs = groceryItemRepository.save(new GroceryItem("Organic Eggs (12pk)", "Dairy", 4.99, 60));
            GroceryItem apples = groceryItemRepository.save(new GroceryItem("Honeycrisp Apples (1kg)", "Produce", 5.49, 35));
            GroceryItem pasta = groceryItemRepository.save(new GroceryItem("Italian Penne Pasta 500g", "Pantry", 1.99, 80));

            // 3. Create Sample Orders
            List<GroceryItem> order1Items = Arrays.asList(milk, bread, eggs);
            double order1Total = order1Items.stream().mapToDouble(GroceryItem::getPrice).sum();
            orderRepository.save(new Order(
                    alice,
                    order1Items,
                    LocalDateTime.now().minusDays(2),
                    Math.round(order1Total * 100.0) / 100.0
            ));

            List<GroceryItem> order2Items = Arrays.asList(apples, pasta);
            double order2Total = order2Items.stream().mapToDouble(GroceryItem::getPrice).sum();
            orderRepository.save(new Order(
                    bob,
                    order2Items,
                    LocalDateTime.now().minusDays(1),
                    Math.round(order2Total * 100.0) / 100.0
            ));

            log.info("Sample data initialized successfully! Customers: 3, Grocery Items: 5, Orders: 2.");
        }
    }
}
