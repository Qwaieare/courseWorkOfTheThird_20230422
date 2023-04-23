package skypro.internetshopsocks.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import skypro.internetshopsocks.customserdeser.CustomTimeDeserializer;
import skypro.internetshopsocks.customserdeser.CustomTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryFile {
    @JsonSerialize(using = CustomTimeSerializer.class) // процесс сохранения
    @JsonDeserialize(using = CustomTimeDeserializer.class) // процесс восстановления
    private LocalDateTime time;
    private SockWarehouse sockWarehouse;
}
