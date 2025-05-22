package br.PI.Pizzaria.repository;

import br.PI.Pizzaria.modelCliente.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
