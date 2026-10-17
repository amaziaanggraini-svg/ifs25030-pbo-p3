package adapter.repository;

import domain.entity.Transaction;
import domain.entity.TransactionType;
import domain.repository.ITransactionRepository;

public class TransactionRepository extends InMemoryRepository<Transaction, Integer> implements ITransactionRepository {
    private int idCounter = 0;

    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction save(String description, long amount, TransactionType type) {
        Transaction transaction = new Transaction(nextId(), description, amount, type);
        return save(transaction);
    }

    private int nextId() {
        return ++idCounter;
    }
}