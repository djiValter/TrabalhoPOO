package controller;

import model.Bilhete;
import model.Cliente;
import model.Compra;
import service.CompraService;

import java.util.List;

public class CompraController {

    private final CompraService compraService;

    public CompraController() {
        this.compraService =
                new CompraService();
    }

    public boolean comprar(
            Cliente cliente,
            Bilhete bilhete,
            int quantidade
    ) {

        return compraService.comprar(
                cliente,
                bilhete,
                quantidade
        );
    }

    public Compra buscarPorId(int id) {

        return compraService.buscarPorId(
                id
        );
    }

    public List<Compra> listarTodos() {

        return compraService.listarTodos();
    }

    public List<Compra> listarPorCliente(
            int clienteId
    ) {

        return compraService.listarPorCliente(
                clienteId
        );
    }

    public List<Compra> listarVendasPorPromotor(
            int promotorId
    ) {

        return compraService.listarVendasPorPromotor(
                promotorId
        );
    }

    public boolean eliminar(int id) {

        return compraService.eliminar(id);
    }
}