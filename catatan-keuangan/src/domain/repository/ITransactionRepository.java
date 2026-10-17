package domain.repository;

import domain.entity.Transaction;
import domain.entity.TransactionType;

public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    Transaction save(String description, long amount, TransactionType type);
}