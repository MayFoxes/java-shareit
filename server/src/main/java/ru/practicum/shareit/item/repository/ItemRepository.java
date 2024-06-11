package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select * " +
            "from Items as it " +
            "where (upper(it.name) like concat('%', upper(?1), '%') " +
            "or upper(it.description) like concat ('%', upper(?1), '%')) " +
            "and it.available = true ", nativeQuery = true)
    List<Item> findByNameContainingIgnoreCase(String nameSearch);

    List<Item> findAllByOwnerOrderByOwner(User owner);

    List<Item> findAllByRequest(Request request);
}
