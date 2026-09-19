package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByClienteOrderByCriadoEmDesc(String cliente);
}
