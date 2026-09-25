package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.ConfiguracaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracaoEmailRepository extends JpaRepository<ConfiguracaoEmail, Long> {
    
    // Buscar primeira configuração (sistema usa apenas uma configuração global)
    Optional<ConfiguracaoEmail> findFirstByOrderByIdAsc();
}
