package skypro.internetshopsocks.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skypro.internetshopsocks.models.*;
import skypro.internetshopsocks.services.SockWarehouseService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "Носки", description = "CRUD операции с носками")
public class SockWarehouseController {
    private final SockWarehouseService sockWarehouseService;

    public SockWarehouseController(SockWarehouseService sockWarehouseService) {
        this.sockWarehouseService = sockWarehouseService;
    }

    @PostMapping
    @Operation(summary = "Поступление новых носков", description = "Регистрирует приход товара на склад")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Socks> addSocks(@RequestBody SockWarehouse sockWarehouse) {
        Socks addSocks = sockWarehouseService.addSocks(sockWarehouse);
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Поступление новых носков", description = "Регистрирует приход товара на склад")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<Socks, Long>> addFilterSocks(
            @RequestParam("выбрать цвет") ColorSocks colorSocks,
            @RequestParam("выбрать размер") SizeSocks sizeSocks,
            @RequestParam("укажите содержание х/б") int cottonPart,
            @RequestParam("укажите количество пар носков") long quantity) {
        sockWarehouseService.addFilterSocks(colorSocks,sizeSocks, cottonPart, quantity);
          return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Получение всех ноcков")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список всех носков получен")})
    public ResponseEntity<Map<Socks, Long>> getAll() {
        return ResponseEntity.ok(sockWarehouseService.getAll());
    }

    @GetMapping
    @Operation(summary = "Запрос носков по параметрам", description = "Получение ноcков, " +
            "отсортированных по цвету, размеру, максимальному значению содержания хлопка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<Socks, Long>> getFilterSockWarehouse(
            @RequestParam("выбрать цвет") ColorSocks colorSocks,
            @RequestParam("выбрать размер") SizeSocks sizeSocks,
            @RequestParam("укажите содержание х/б") int cottonPart,
            @RequestParam("укажите количество пар носков") long quantity )
    {
        return ResponseEntity.ok(sockWarehouseService.getFilterSockWarehouse(colorSocks,
                sizeSocks, cottonPart, quantity));
    }

    @PutMapping
    @Operation(summary = "Отпуск носков со склада по параметрам", description = "Уменьшение общего количества носков")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Socks> editFilterSockWarehouse(@RequestBody Socks socks, @RequestParam long quantity)
    {
        Socks socks1 = sockWarehouseService.editFilterSockWarehouse(socks, quantity);
        if (socks1 == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(socks1);
    }

    @DeleteMapping
    @Operation(summary = "Списание бракованных носков", description = "Уменьшение общего количества носков")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Long> deleteSockWarehouse(@RequestBody SockWarehouse sockWarehouse) {
        sockWarehouseService.deleteSockWarehouse(sockWarehouse);
        return ResponseEntity.ok().build();
    }

}
