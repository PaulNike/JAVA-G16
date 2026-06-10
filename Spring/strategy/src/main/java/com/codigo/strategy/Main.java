package com.codigo.strategy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(StrategyApplication.class, args);
    }
    private final PedidoService pedidoService;

    public Main(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Strategy Pattern con Spring Boot ===");
        pedidoService.procesarPedido("VIP", 1000);
        pedidoService.procesarPedido("NUEVO", 1000);
        pedidoService.procesarPedido("OTRO", 1000);

    }
}
