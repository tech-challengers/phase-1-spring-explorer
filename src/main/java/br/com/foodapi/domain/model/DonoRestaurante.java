package br.com.foodapi.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DONO_RESTAURANTE")
@Getter
@Setter
@NoArgsConstructor

public class DonoRestaurante extends Usuario {

}