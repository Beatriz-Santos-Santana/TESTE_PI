package br.PI.Pizzaria.repository;

import br.PI.Pizzaria.modelProdutos.ImagemProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemProdutoRepository extends JpaRepository<ImagemProduto, Long> {
}
