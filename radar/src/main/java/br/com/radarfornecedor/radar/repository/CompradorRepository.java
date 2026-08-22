package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.Comprador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {
    Optional<Comprador> findByCnpj(String cnpj);
}
