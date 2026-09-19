package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.Representante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepresentanteRepository extends JpaRepository<Representante, Long> {
    List<Representante> findByCnpjFornecedor(String cnpjFornecedor);
    List<Representante> findByCodEmpresaAndCnpjFornecedor(Long codEmpresa, String cnpjFornecedor);
}
