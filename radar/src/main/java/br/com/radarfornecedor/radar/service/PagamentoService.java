package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.dto.PagamentoRequest;
import br.com.radarfornecedor.radar.model.*;
import br.com.radarfornecedor.radar.repository.PagamentoRepository;
import br.com.radarfornecedor.radar.repository.ProdutoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;
    private final ProdutoRepository produtoRepository;
    public PagamentoService(PagamentoRepository pagamentoRepository, ProdutoRepository produtoRepository) { this.pagamentoRepository = pagamentoRepository; this.produtoRepository = produtoRepository; }

    public Pagamento criar(PagamentoRequest dados, HttpSession session) {
        Usuario usuario = clienteLogado(session);
        Produto produto = produtoRepository.findById(dados.produtoId()).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (produto.getFornecedorId() == null || produto.getPreco() == null) throw new IllegalArgumentException("O produto precisa ter empresa e preço cadastrados antes da compra.");
        String cep = dados.cepEntrega().replaceAll("\\D", "");
        if (cep.length() != 8) throw new IllegalArgumentException("Informe um CEP de entrega válido.");
        Pagamento pagamento = new Pagamento();
        pagamento.setProdutoId(produto.getId()); pagamento.setFornecedorId(produto.getFornecedorId()); pagamento.setCliente(usuario.getUsername());
        pagamento.setForma(dados.forma()); pagamento.setValor(produto.getPreco()); pagamento.setCepEntrega(cep);
        if (dados.forma() == FormaPagamento.BOLETO_BANCARIO) pagamento.setVencimentoBoleto(proximoDiaUtil().atTime(LocalTime.MAX));
        return pagamentoRepository.save(pagamento);
    }

    public Pagamento confirmar(Long id, HttpSession session) {
        Usuario usuario = clienteLogado(session);
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado."));
        if (!pagamento.getCliente().equals(usuario.getUsername())) throw new IllegalStateException("Você não pode confirmar este pagamento.");
        atualizarExpiracao(pagamento);
        if (pagamento.getStatus() == StatusPagamento.EXPIRADO) throw new IllegalStateException("O boleto venceu. A compra não foi realizada.");
        pagamento.setStatus(StatusPagamento.CONFIRMADO);
        return pagamentoRepository.save(pagamento);
    }

    public List<Pagamento> listarDoCliente(HttpSession session) {
        List<Pagamento> pagamentos = pagamentoRepository.findByClienteOrderByCriadoEmDesc(clienteLogado(session).getUsername());
        pagamentos.forEach(this::atualizarExpiracao);
        return pagamentoRepository.saveAll(pagamentos);
    }
    private void atualizarExpiracao(Pagamento pagamento) { if (pagamento.getForma() == FormaPagamento.BOLETO_BANCARIO && pagamento.getStatus() == StatusPagamento.PENDENTE && LocalDateTime.now().isAfter(pagamento.getVencimentoBoleto())) pagamento.setStatus(StatusPagamento.EXPIRADO); }
    private Usuario clienteLogado(HttpSession session) { Usuario usuario = (Usuario) session.getAttribute("usuario"); if (usuario == null || usuario.getTipo() != TipoUsuario.CLIENTE) throw new IllegalStateException("Apenas clientes podem realizar pagamentos."); return usuario; }
    private LocalDate proximoDiaUtil() { LocalDate data = LocalDate.now().plusDays(1); while (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY) data = data.plusDays(1); return data; }
}
