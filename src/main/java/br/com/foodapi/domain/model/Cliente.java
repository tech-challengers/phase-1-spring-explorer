package br.com.foodapi.domain.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Usuario {

}