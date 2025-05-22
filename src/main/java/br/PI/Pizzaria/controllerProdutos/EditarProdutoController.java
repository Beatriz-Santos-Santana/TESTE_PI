package br.PI.Pizzaria.controllerProdutos;

import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.repository.ProdutoRepository;
import br.PI.Pizzaria.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.modelProdutos.ImagemProduto;
import br.PI.Pizzaria.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class EditarProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Exibe o formulário de edição
    @GetMapping("/editar-produto/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        model.addAttribute("produto", produto);
        return "editarProduto"; // Thymeleaf: templates/editarProduto.html
    }

    // Processa o formulário de edição
    @PostMapping("/editar-produto/{id}")
    public String salvarAlteracoes(@PathVariable Long id,
                                   @ModelAttribute Produto produtoForm,
                                   @RequestParam(value = "novasImagens", required = false) List<MultipartFile> novasImagens,
                                   @RequestParam(value = "imagemPrincipal", required = false) String imagemPrincipal) {

        try {
            Produto produtoExistente = produtoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            produtoExistente.setNome(produtoForm.getNome());
            produtoExistente.setDescricao(produtoForm.getDescricao());
            produtoExistente.setQuantidade(produtoForm.getQuantidade());
            produtoExistente.setValor(produtoForm.getValor());
            produtoExistente.setAvaliacao(produtoForm.getAvaliacao());

            if (imagemPrincipal != null && !imagemPrincipal.isEmpty()) {
                produtoExistente.setImagem(imagemPrincipal);
            }

            if (novasImagens != null && !novasImagens.isEmpty()) {
                for (MultipartFile arquivo : novasImagens) {
                    if (!arquivo.isEmpty()) {
                        String nomeArquivo = arquivo.getOriginalFilename();
                        Path uploadDir = Paths.get("uploads/imagens/");
                        if (!Files.exists(uploadDir)) {
                            Files.createDirectories(uploadDir);
                        }
                        Path caminho = uploadDir.resolve(nomeArquivo);
                        Files.write(caminho, arquivo.getBytes());

                        produtoExistente.adicionarImagemSecundaria(nomeArquivo);
                    }
                }
            }

            produtoRepository.save(produtoExistente);
            return "redirect:/listar-produtos";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/erro";
        }
    }

}
