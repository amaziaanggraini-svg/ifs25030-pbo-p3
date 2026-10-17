package adapter.repository;

import domain.repository.IRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Implementasi generic {@link IRepository} yang menyimpan data di memori (ArrayList).
 * ID setiap entity diambil melalui {@code idExtractor} sehingga kelas ini
 * dapat dipakai ulang oleh semua repository konkret.
 */
public abstract class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    protected final List<T> data = new ArrayList<>();
    private final Function<T, ID> idExtractor;

    protected InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Optional<T> findById(ID id) {
        return data.stream()
                .filter(item -> idExtractor.apply(item).equals(id))
                .findFirst();
    }

    @Override
    public T save(T entity) {
        data.add(entity);
        return entity;
    }

    @Override
    public boolean deleteById(ID id) {
        return data.removeIf(item -> idExtractor.apply(item).equals(id));
    }

    /**
     * Mengganti entity lama yang memiliki ID sama dengan entity baru.
     * Posisi entity di dalam daftar tidak berubah. Jika ID tidak ditemukan,
     * tidak ada yang diubah (entity baru tidak ditambahkan; gunakan {@link #save(Object)}).
     */
    @Override
    public void update(T entity) {
        ID id = idExtractor.apply(entity);
        for (int index = 0; index < data.size(); index++) {
            if (idExtractor.apply(data.get(index)).equals(id)) {
                data.set(index, entity);
                return;
            }
        }
    }

    @Override
    public List<T> findBy(Predicate<T> predicate) {
        return data.stream()
                .filter(predicate)
                .toList();
    }
}
