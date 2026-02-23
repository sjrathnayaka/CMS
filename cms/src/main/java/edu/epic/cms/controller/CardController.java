package edu.epic.cms.controller;

import edu.epic.cms.api.UpdateCardRequest;
import edu.epic.cms.model.Card;
import edu.epic.cms.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    
    private final CardService cardService;
    
    @GetMapping
    public String listCards(Model model) {
        List<Card> cards = cardService.getAllCards();
        model.addAttribute("cards", cards);
        return "cards/list";
    }
    
    @GetMapping("/add")
    public String showAddCardForm(Model model) {
        model.addAttribute("card", new Card());
        return "cards/add";
    }
    
    @PostMapping("/add")
    public String addCard(@Valid @ModelAttribute("card") Card card, 
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "cards/add";
        }
        
        try {
            Card savedCard = cardService.addCard(card);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Card added successfully with Card Number: " + savedCard.getCardNumber());
            return "redirect:/cards/success";
        } catch (IllegalArgumentException e) {
            result.rejectValue("cardNumber", "error.card", e.getMessage());
            return "cards/add";
        }
    }
    
    @GetMapping("/success")
    public String showSuccessPage() {
        return "cards/success";
    }
    
    @GetMapping("/edit/{cardNumber}")
    public String showEditCardForm(@PathVariable String cardNumber, Model model) {
        try {
            Card card = cardService.getCardByCardNumber(cardNumber);
            
            // Create UpdateCardRequest from Card for form binding
            UpdateCardRequest updateRequest = new UpdateCardRequest();
            updateRequest.setExpiryDate(card.getExpiryDate());
            updateRequest.setCreditLimit(card.getCreditLimit());
            updateRequest.setCashLimit(card.getCashLimit());
            
            model.addAttribute("card", card);
            model.addAttribute("updateRequest", updateRequest);
            return "cards/edit";
        } catch (IllegalArgumentException e) {
            return "redirect:/cards/add";
        }
    }
    
    @PostMapping("/edit/{cardNumber}")
    public String updateCard(@PathVariable String cardNumber,
                           @Valid @ModelAttribute("updateRequest") UpdateCardRequest updateRequest,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            Card card = cardService.getCardByCardNumber(cardNumber);
            model.addAttribute("card", card);
            return "cards/edit";
        }
        
        try {
            Card updatedCard = cardService.updateCard(cardNumber, updateRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Card updated successfully! Card Number: " + updatedCard.getCardNumber());
            return "redirect:/cards/success";
        } catch (IllegalArgumentException e) {
            Card card = cardService.getCardByCardNumber(cardNumber);
            model.addAttribute("card", card);
            model.addAttribute("errorMessage", e.getMessage());
            return "cards/edit";
        }
    }
}
