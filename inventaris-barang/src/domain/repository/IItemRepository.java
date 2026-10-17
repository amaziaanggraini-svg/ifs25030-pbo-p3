package domain.repository;

import domain.entity.Item;

public interface IItemRepository extends IRepository<Item, Integer> {
    Item save(String name, int quantity, String category);
}