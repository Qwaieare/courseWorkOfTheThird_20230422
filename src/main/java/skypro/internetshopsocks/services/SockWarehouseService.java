package skypro.internetshopsocks.services;

import org.springframework.validation.annotation.Validated;
import skypro.internetshopsocks.models.*;

import javax.validation.ValidationException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Validated
public interface SockWarehouseService {

   Socks addSocks(SockWarehouse sockWarehouse);

    Map<Socks, Long> addFilterSocks(ColorSocks colorSocks, SizeSocks sizeSocks,
                                    int cottonPart, long quantity)
            throws ValidationException;

    Map<Socks, Long> getAll();

    Map<Socks, Long> getFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks,
                                            int cottonPart, long quantity)
            throws ValidationException;

    Socks editFilterSockWarehouse(Socks socks, long quantity);

    long deleteSockWarehouse(SockWarehouse sockWarehouse);


    Path createReport() throws IOException, NoSuchElementException;
}
