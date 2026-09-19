package br.com.radarfornecedor.radar.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Atividades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atividade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cnae", length = 10, unique = true)
    private String cnae; // Código Nacional de Atividade Econômica
    
    @Column(name = "descricao", nullable = false, unique = true)
    private String descricao;
    
    @Column(name = "secao", length = 5)
    private String secao; // Ex: A, B, C...
    
    @Column(name = "divisao", length = 5)
    private String divisao; // Ex: 01, 02, 03...
    
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
