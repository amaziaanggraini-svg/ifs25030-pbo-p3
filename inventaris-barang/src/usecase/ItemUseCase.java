package usecase;

import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * Use case logika bisnis inventaris barang.
 * Tidak melakukan I/O; hanya melempar checked exception domain.
 */
public class ItemUseCase {
    private final IItemRepository itemRepository;

    public ItemUseCase(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item addItem(String name, int quantity, String category) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama barang tidak boleh kosong");
        }
        if (quantity <= 0) {
            throw new ValidationException("Jumlah stok tidak valid");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Kategori tidak boleh kosong");
        }
        return itemRepository.save(name, quantity, category);
    }

    /**
     * Mengubah stok barang. Parameter {@code newQuantity} bernilai null
     * berarti stok tidak diubah (update parsial).
     */
    public void updateItemStock(int id, Integer newQuantity)
            throws EntityNotFoundException, ValidationException {
        Optional<Item> found = itemRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        Item item = found.get();

        if (newQuantity != null) {
            if (newQuantity <= 0) {
                throw new ValidationException("Jumlah stok tidak valid");
            }
            item.changeQuantity(newQuantity);
        }
        itemRepository.update(item);
    }

    public void removeItem(int id) throws EntityNotFoundException {
        boolean deleted = itemRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    public List<Item> searchItems(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();
        return itemRepository.findBy(item -> item.getName().toLowerCase().contains(lowerKeyword));
    }

    public List<Item> sortItems(SortOption option) {
        return itemRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }
}
