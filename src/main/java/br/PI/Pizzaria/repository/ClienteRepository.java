package br.PI.Pizzaria.repository;

import br.PI.Pizzaria.modelCliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByEmail(String email);
    Cliente findByCpf(String cpf);
    Optional<Cliente> findById(long id);
    Optional<Cliente> findByNome(String nome);
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.enderecos WHERE c.email = :email")
    Cliente findByEmailWithEnderecos(@Param("email") String email);


}
