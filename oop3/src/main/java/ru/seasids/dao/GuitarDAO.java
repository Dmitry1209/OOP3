package ru.seasids.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.seasids.Guitar;
import ru.seasids.dao.GenericDAO;

public class GuitarDAO extends GenericDAO {
    public void save(Guitar guitar) {
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            session.save(guitar);
            tx.commit();
            System.out.println("Гитара сохранена с ID: " + guitar.getId());
        }
    }

    public void delete(Guitar guitar) {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Guitar merged = (Guitar) session.merge(guitar);
            session.delete(merged);
            tx.commit();
            System.out.println("Гитара с ID: " + guitar.getId() + " удалена");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Ошибка при удалении гитары", e);
        } finally {
            session.close();
        }
    }
}