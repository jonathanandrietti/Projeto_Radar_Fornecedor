package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.Representante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepresentanteRepository extends JpaRepository<Representante, Long> {
}
