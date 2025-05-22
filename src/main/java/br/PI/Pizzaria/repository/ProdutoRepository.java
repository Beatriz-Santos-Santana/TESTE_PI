package br.PI.Pizzaria.repository;

import br.PI.Pizzaria.modelProdutos.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Busca produtos pelo nome com busca parcial, ignorando maiúsculas/minúsculas
    Page<Produto> findByNomeContainingIgnoreCase(String nome, PageRequest pageRequest);

    // Busca produtos pelo nome, categoria e apenas produtos ativos
    List<Produto> findByNomeContainingIgnoreCaseAndCategoriaContainingIgnoreCaseAndAtivoTrue(String nome, String categoria);

    // Busca produtos apenas pela categoria e produtos ativos
    List<Produto> findByCategoriaContainingIgnoreCaseAndAtivoTrue(String categoria);

    // Busca produtos ativos
    List<Produto> findByAtivoTrue();

    // Busca produtos pelo nome com busca parcial, ignorando maiúsculas/minúsculas
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    List<Produto> findByNomeContainingIgnoreCaseAndNomeContainingIgnoreCaseAndAtivoTrue(String nome, String categoria);

    // Retorna todas as categorias distintas
    @Query("SELECT DISTINCT p.categoria FROM Produto p")
    List<String> findDistinctCategorias();




}
