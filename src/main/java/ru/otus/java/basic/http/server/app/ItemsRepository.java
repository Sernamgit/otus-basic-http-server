package ru.otus.java.basic.http.server.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemsRepository {
    private static final Logger logger = LogManager.getLogger(ItemsRepository.class);

    private List<Item> items;

    public List<Item> getItems() {
        logger.info("Получен запрос на список всех товаров. Количество товаров: {}", items.size());
        return Collections.unmodifiableList(items);
    }

    public ItemsRepository() {
        this.items = new ArrayList<>(Arrays.asList(
                new Item(1L, "Milk", BigDecimal.valueOf(80)),
                new Item(2L, "Bread", BigDecimal.valueOf(32)),
                new Item(3L, "Cheese", BigDecimal.valueOf(320))
        ));
    }

    public Item add(Item item) {
        Long newId = items.stream().mapToLong(Item::getId).max().orElse(0L) + 1L;
        item.setId(newId);
        items.add(item);
        logger.info("Добавлен новый товар: {} с ID: {}", item.getTitle(), item.getId());
        return item;
    }
}
