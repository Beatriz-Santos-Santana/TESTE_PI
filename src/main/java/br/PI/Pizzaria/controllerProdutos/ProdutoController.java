package br.PI.Pizzaria.controllerProdutos;

import br.PI.Pizzaria.modelProdutos.ImagemProduto;
import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.repository.ImagemProdutoRepository;
import br.PI.Pizzaria.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Controller
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ImagemProdutoRepository imagemProdutoRepository;

    private final Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "imagens");

    // Método GET para exibir o formulário de cadastro de produto
    @GetMapping("/cadastroProduto")
    public String exibirFormularioCadastro(@RequestParam(required = false) Long id, Model model) {
        Produto produto = (id != null) ? produtoRepository.findById(id).orElse(new Produto()) : new Produto();
        model.addAttribute("produto", produto);
        model.addAttribute("avaliacoes", new Integer[]{1, 2, 3, 4, 5});
        return "cadastroProduto";
    }

    // Método GET para exibir a lista de produtos com paginação e suporte à busca
    @GetMapping("/listar-produtos")
    public String listarProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String buscaProduto,
            Model model) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        Page<Produto> produtosPage;

        if (buscaProduto != null && !buscaProduto.isEmpty()) {
            produtosPage = produtoRepository.findByNomeContainingIgnoreCase(buscaProduto, pageRequest);
        } else {
            produtosPage = produtoRepository.findAll(pageRequest);
        }

        model.addAttribute("produtos", produtosPage.getContent());
        model.addAttribute("totalPages", produtosPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("buscaProduto", buscaProduto);

        return "listar-produtos";
    }

    @GetMapping("/alterar-status/{id}")
    public String alterarStatus(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setAtivo(!(produto.getAtivo() != null && produto.getAtivo()));

        produtoRepository.save(produto);
        return "redirect:/listar-produtos";
    }

    @PostMapping("/salvar-produto")
    public String salvarProduto(@ModelAttribute Produto produto,
                                @RequestParam("imagens") List<MultipartFile> imagens,
                                @RequestParam(value = "imagemPrincipal", required = false) Integer imagemPrincipalIndex,
                                @RequestParam(value = "finalizar", required = false) Boolean finalizar,
                                Model model) {
        try {
            System.out.println("Iniciando salvamento do produto...");
            boolean novoProduto = produto.getId() == null || produto.getId() <= 0;

            if (novoProduto) {
                System.out.println("Criando novo produto...");
                produto.setFinalizado(false);
                produto = produtoRepository.save(produto);
                System.out.println("Novo produto salvo com ID: " + produto.getId());
            } else {
                System.out.println("Atualizando produto existente...");
                Produto existente = produtoRepository.findById(produto.getId()).orElseThrow();
                produto.setImagem(existente.getImagem());
            }

            System.out.println("Verificando diretório para upload...");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Diretório criado: " + uploadPath.toAbsolutePath());
            }

            // Crie a lista de imagens secundárias
            List<ImagemProduto> novasImagens = new ArrayList<>();

            for (int i = 0; i < imagens.size(); i++) {
                MultipartFile imagem = imagens.get(i);
                if (!imagem.isEmpty()) {
                    String nomeArquivo = UUID.randomUUID() + "_" + imagem.getOriginalFilename();
                    Path caminhoArquivo = uploadPath.resolve(nomeArquivo);
                    Files.copy(imagem.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

                    // Verifica se o índice da imagem principal é válido
                    if (imagemPrincipalIndex != null && imagemPrincipalIndex >= 0 && imagemPrincipalIndex < imagens.size() && i == imagemPrincipalIndex) {
                        produto.setImagem(nomeArquivo);
                    } else {
                        ImagemProduto imagemSecundaria = new ImagemProduto();
                        imagemSecundaria.setCaminhoImagem(nomeArquivo);
                        imagemSecundaria.setProduto(produto); // RELAÇÃO

                        novasImagens.add(imagemSecundaria);
                    }
                }
            }

            // Limpe imagens antigas, se necessário
            produto.getImagensSecundarias().clear();

            // Atualize a relação bidirecional
            for (ImagemProduto img : novasImagens) {
                produto.getImagensSecundarias().add(img); // necessário para manter sincronizado
            }

            produto.setFinalizado(finalizar != null && finalizar);

            // Aqui o JPA deve persistir o produto e suas imagens secundárias
            produtoRepository.save(produto);
            produto.getImagensSecundarias().addAll(novasImagens);

            System.out.println("Produto salvo com sucesso. Finalizar: " + finalizar);

            if (finalizar != null && !finalizar) {
                return "redirect:/cadastroProduto?id=" + produto.getId();
            }

            return "redirect:/listar-produtos";

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao salvar imagens.");
            return "cadastroProduto";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro inesperado ao salvar produto.");
            return "cadastroProduto";
        }
    }



}
