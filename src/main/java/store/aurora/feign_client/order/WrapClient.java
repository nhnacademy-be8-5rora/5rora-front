package store.aurora.feign_client.order;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.order.dto.WrapResponseDTO;

import java.util.List;

@FeignClient(name = "wrapClient", url = "${api.gateway.base-url}" + "/api/order/wrap")
public interface WrapClient {

    @GetMapping
    ResponseEntity<List<WrapResponseDTO>> getWrapList();

    @PostMapping
    void createWrap(@RequestBody WrapResponseDTO wrap);

    @PatchMapping
    void updateWrap(@RequestBody WrapResponseDTO wrap);

    @DeleteMapping
    void deleteWrap(@RequestParam Long id);
}
