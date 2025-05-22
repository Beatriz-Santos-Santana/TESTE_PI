package br.PI.Pizzaria.repository;

import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.modelUsuarios.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> { // ALTERADO DE CrudRepository PARA JpaRepository
    Optional<Usuario> findById(long id);
    Optional<Usuario> findByEmail(String email);;


    // Novo método para buscar usuários ativos ou inativos
    //List<Usuario> findByAtivo(boolean ativo);

    // Busca uuários pelo nome com busca parcial, ignorando maiúsculas/minúsculas
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

}


