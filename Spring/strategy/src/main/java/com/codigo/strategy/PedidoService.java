package com.codigo.strategy;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {
    private final List<DescuentoStrategy> strategies;

    public PedidoService(List<DescuentoStrategy> strategies) {
        this.strategies = strategies;
    }

    public void procesarPedido(String tipoCliente, double precio) {
        //Buscamos la estrategia
        DescuentoStrategy strategy = strategies.stream()
                .filter( s -> s.nombre().toUpperCase().contains(tipoCliente.toUpperCase()))
                .findFirst()
                .orElseGet(SinDescuento::new);

        double descuento = strategy.aplicar(precio);

        System.out.printf("Cliente: %-8s | %s | S/. %.2f%n",
                tipoCliente, strategy.nombre(), descuento);
    }
}
