package br.PI.Pizzaria.repository;

import br.PI.Pizzaria.modelPedidos.Pedidos;
import br.PI.Pizzaria.modelPedidos.StatusPedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedidos, Long> {
    List<Pedidos> findByClienteId(Long clienteId);

    @Query("SELECT p FROM Pedidos p LEFT JOIN FETCH p.itens WHERE p.id = :pedidoId")
    Optional<Pedidos> buscarComItens(@Param("pedidoId") Long pedidoId);

    @EntityGraph(attributePaths = {"itens"})
    Optional<Pedidos> findWithItensById(Long id);

    Optional<Pedidos> findTopByClienteIdAndStatusOrderByDataDesc(Long clienteId, StatusPedido status);


}

