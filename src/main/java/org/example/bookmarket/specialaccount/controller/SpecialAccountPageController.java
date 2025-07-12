package org.example.bookmarket.specialaccount.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmarket.specialaccount.dto.SpecialAccountRequest;
import org.example.bookmarket.specialaccount.entity.SpecialAccount;
import org.example.bookmarket.specialaccount.service.SpecialAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/special-accounts")
public class SpecialAccountPageController {
    private final SpecialAccountService service;

    @GetMapping
    public String list(@RequestParam(name = "all", defaultValue = "false") boolean all, Model model) {
        model.addAttribute("accounts", service.getAccounts(all));
        model.addAttribute("accountForm", new SpecialAccountRequest());
        model.addAttribute("showAll", all);
        return "special-account/manager";
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable Long id, @RequestParam(name = "all", defaultValue = "false") boolean all, Model model) {
        model.addAttribute("accounts", service.getAccounts(all));
        SpecialAccount acc = service.getAccount(id);
        SpecialAccountRequest req = new SpecialAccountRequest(acc.getId(), acc.getNickname(), acc.getStatus());
        model.addAttribute("accountForm", req);
        model.addAttribute("showAll", all);
        return "special-account/manager";
    }

    @PostMapping
    public String create(@ModelAttribute SpecialAccountRequest account, @RequestParam(name = "all", defaultValue = "false") boolean all) {
        service.saveAccount(account);
        return "redirect:/admin/special-accounts" + (all ? "?all=true" : "");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam(name = "all", defaultValue = "false") boolean all) {
        service.deleteAccount(id);
        return "redirect:/admin/special-accounts" + (all ? "?all=true" : "");
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, @RequestParam(name = "all", defaultValue = "false") boolean all) {
        service.toggleAccountStatus(id);
        return "redirect:/admin/special-accounts" + (all ? "?all=true" : "");
    }
}