package skypro.internetshopsocks.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SockWarehouse {
    private Socks socks;

    @PositiveOrZero(message = "Количество должно быть больше нуля")
    private long quantity;

}
