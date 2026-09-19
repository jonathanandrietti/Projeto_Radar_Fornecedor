package br.com.radarfornecedor.radar.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome", nullable = false, unique = true)
    private String nome;
    
    @Column(name = "descricao", length = 500)
    private String descricao;
    
    @Column(name = "icone", length = 50)
    private String icone;
    
    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;
    
    @Column(name = "criadaEm", nullable = false)
    private LocalDateTime criadaEm;
    
    @Column(name = "atualizadaEm")
    private LocalDateTime atualizadaEm;
    
    @PrePersist
    protected void onCreate() {
        criadaEm = LocalDateTime.now();
        atualizadaEm = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        atualizadaEm = LocalDateTime.now();
    }
}
