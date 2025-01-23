package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feign_client.order.WrapClient;
import store.aurora.order.dto.WrapResponseDTO;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/wrap")
public class AdminWrapController {
    private final WrapClient wrapClient;

    private static final String REDIRECT_WRAP = "redirect:/admin/wrap";

    @GetMapping
    public String getWrapList(Model model){
        List<WrapResponseDTO> wrapList = wrapClient.getWrapList().getBody();
        if(Objects.nonNull(wrapList)){
            wrapList.removeFirst();
        } else{
            wrapList = List.of();
        }

        model.addAttribute("wrapList", wrapList);

        return "admin/order/wrap";
    }

    @PostMapping("/update-wrap")
    public String updateWrap(WrapResponseDTO wrap){
        wrapClient.updateWrap(wrap);

        return REDIRECT_WRAP;
    }

    @PostMapping
    public String createWrap(WrapResponseDTO wrap){
        wrap.setId(-1);
        wrapClient.createWrap(wrap);

        return REDIRECT_WRAP;
    }

    @PostMapping("/delete")
    public String deleteWrap(Long id){
        wrapClient.deleteWrap(id);

        return REDIRECT_WRAP;
    }
}
