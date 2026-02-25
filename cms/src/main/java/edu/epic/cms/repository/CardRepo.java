package edu.epic.cms.repository;

import edu.epic.cms.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepo {

    Card save(Card card);

    Optional<Card> findById(Long id);

    Optional<Card> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    List<Card> findAll();

    List<Card> findAllFiltered(String status, java.time.LocalDate fromDate, java.time.LocalDate toDate);

    int update(Card card);

    int deleteById(Long id);
}
