package br.PI.Pizzaria.controllerCardapio;

import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.repository.ProdutoRepository;
import br.PI.Pizzaria.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class ControllerCardapio {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/cardapio")
    public String cardapio(
            @RequestParam(required = false) String pesquisa,
            @RequestParam(required = false) String categoria,
            HttpServletRequest request,
            Model model) {

        List<Produto> produtos;

        // Caso a pesquisa seja fornecida, filtra pelo nome
        if (pesquisa != null && !pesquisa.isEmpty()) {
            // Se a categoria também for fornecida, filtra pelo nome e pela categoria (categoria como substring no nome)
            if (categoria != null && !categoria.isEmpty()) {
                produtos = produtoRepository.findByNomeContainingIgnoreCaseAndNomeContainingIgnoreCaseAndAtivoTrue(pesquisa, categoria);
            } else {
                // Apenas pesquisa no nome
                produtos = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(pesquisa);
            }
        } else if (categoria != null && !categoria.isEmpty()) {
            // Se apenas categoria for fornecida, filtra pelo nome (categoria sendo tratada como parte do nome)
            produtos = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(categoria);
        } else {
            // Se nada for fornecido, exibe todos os produtos ativos
            produtos = produtoRepository.findByAtivoTrue();
        }

        model.addAttribute("produtos", produtos);

        // Adiciona o nome do cliente no modelo (se estiver disponível)
        try {
            String nomeCliente = CookieService.getCookie(request, "clienteNome");
            model.addAttribute("nomeCliente", nomeCliente);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "cardapio";
    }



    @GetMapping("/detalheProduto")
    public String detalheProduto() {
        return "detalheProduto";
    }

    @GetMapping("/frete")
    public String frete() {
        return "frete";
    }
}
