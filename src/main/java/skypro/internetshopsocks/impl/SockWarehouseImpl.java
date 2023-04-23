package skypro.internetshopsocks.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import skypro.internetshopsocks.models.*;
import skypro.internetshopsocks.services.FileService;
import skypro.internetshopsocks.services.SockWarehouseService;

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class SockWarehouseImpl implements SockWarehouseService  {
    private Map<Socks, Long> batchOfSsocks = new HashMap<>();
    private List<SockWarehouse> socksList = new ArrayList<>();
    private static Map<HistoryFile, Long> transactions = new LinkedHashMap<>();
    private final FileService fileService;

    public SockWarehouseImpl(FileService fileService) {
        this.fileService = fileService;
    }


    @Override
    public Socks addSocks(SockWarehouse sockWarehouse) { // поступление носков на склад
        Socks socks = sockWarehouse.getSocks();
        if (batchOfSsocks.containsKey(socks)) {
            batchOfSsocks.replace(socks, batchOfSsocks.get(socks) + sockWarehouse.getQuantity());
        } else {
            batchOfSsocks.put(socks, sockWarehouse.getQuantity());
        }
        saveToFile();
        return socks;
    }
    @Override
    public Map<Socks, Long> addFilterSocks(ColorSocks colorSocks, SizeSocks sizeSocks,
                                           int cottonPart, long quantity)
            throws ValidationException {
        for (Map.Entry<Socks, Long> map : batchOfSsocks.entrySet()) {
            Socks socks = map.getKey();
            if (socks.getColorSocks().equals(colorSocks) &&
                    socks.getSizeSocks().equals(sizeSocks) &&
                    socks.getCottonPart() == cottonPart &&
                    "quantity".equals(map.getValue())) {
            }
            batchOfSsocks.replace(socks, batchOfSsocks.get(socks));
            batchOfSsocks.put(map.getKey(), map.getValue());
        }
        return null;
    }

    @Override
    public Map<Socks, Long> getAll() {
        return null;
    }

    @Override
    public Map<Socks, Long> getFilterSockWarehouse(ColorSocks colorSocks, SizeSocks sizeSocks,
                                                   int cottonPart, long quantity)
            throws ValidationException {
        if (batchOfSsocks.containsValue(quantity)) {

            Map<Socks, Long> socksFilter = batchOfSsocks.entrySet()
                    .stream()
                    .filter(map -> map.getKey().getColorSocks().equals(colorSocks))
                    .filter(map -> map.getKey().getSizeSocks().equals(sizeSocks))
                    .filter(map -> map.getKey().getCottonPart() == cottonPart)
                    .filter(map -> "quantity".equals(map.getValue()))
                    .collect(Collectors
                            .toMap(Entry::getKey,
                                    Entry::getValue)
                    );
            return socksFilter;
        }
        return Collections.EMPTY_MAP;
    }
    @Override
    public Socks editFilterSockWarehouse(Socks socks, long quantity) { // отпуск носков со склада
        if (batchOfSsocks.containsValue(quantity)) {
            long extraditionSocks = batchOfSsocks.get(socks) - quantity;
            if (extraditionSocks > 0) {
                batchOfSsocks.merge(socks, quantity, (a, b) -> a - b);
                batchOfSsocks.putIfAbsent(socks, quantity);
            }
            saveToFile();
        } else {
            throw new IllegalArgumentException();
        }
        return socks;
    }

    @Override
    public long deleteSockWarehouse(SockWarehouse sockWarehouse) {
        Socks socks = sockWarehouse.getSocks();
        long quantity = batchOfSsocks.get(socks);
        if (batchOfSsocks.containsKey(socks)) {
            if (quantity > sockWarehouse.getQuantity()) {
                batchOfSsocks.replace(socks, quantity - sockWarehouse.getQuantity());
                return sockWarehouse.getQuantity();
            } else {
                batchOfSsocks.remove(socks);
                return quantity;
            }
        }
        saveToFile();
        return 0;
    }

    // метод принимает и сохраняет информацию
    private void saveToFile() {
        for (Entry<Socks, Long> socksItem : batchOfSsocks.entrySet()) {
            socksList.add(new SockWarehouse(socksItem.getKey(), socksItem.getValue()));
        }
        try {
            String json = new ObjectMapper().writeValueAsString(socksList);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // метод возвращает информацию, которую прочитали
    private void readFromFile() {
        String json = fileService.readFromFile();
        try {
            socksList = new ObjectMapper().readValue(json,
                         new TypeReference<ArrayList<SockWarehouse>>() {
                         });
        } catch (JsonMappingException e) {
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        socksList.forEach(
                (n) -> { batchOfSsocks.put(n.getSocks(), n.getQuantity()); }
        );
    }
    @Override
    public Path createReport() throws IOException, NoSuchElementException {
        Path report = fileService.createTempFile("report");
        try (Writer writer = Files.newBufferedWriter(report, StandardOpenOption.APPEND)) {
            for (Entry<HistoryFile, Long> reportSocks : transactions.entrySet()) {
                writer.append(reportSocks.getKey().toString())
                        .append("Время: ")
                        .append(reportSocks.getKey().getTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))
                        .append(", Количество: ")
                        .append((char) reportSocks.getKey().getSockWarehouse().getQuantity())
                        .append(", Размер: ")
                        .append(reportSocks.getKey().getSockWarehouse().getSocks().getSizeSocks().name())
                        .append(", Содержание хлопка: ")
                        .append(String.valueOf(reportSocks.getKey().getSockWarehouse().getSocks().getCottonPart()))
                        .append("%, Цвет: ")
                        .append(reportSocks.getKey().getSockWarehouse().getSocks().getColorSocks().name())
                        .append(System.lineSeparator());
            }
        }
        return report;
    }

    @PostConstruct
    private void init() {
        try {
            readFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
