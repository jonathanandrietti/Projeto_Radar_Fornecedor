package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByTipoPessoa(String tipoPessoa);
}
