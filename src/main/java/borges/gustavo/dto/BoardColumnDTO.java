package borges.gustavo.dto;

import borges.gustavo.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind,
                             int cards_amount) {


}
