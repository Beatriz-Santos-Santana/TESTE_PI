package br.PI.Pizzaria.repository;

import br.PI.Pizzaria.modelPedidos.ItemPedidos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedidos, Long> {
    List<ItemPedidos> findByPedidoId(Long pedidoId);

}
