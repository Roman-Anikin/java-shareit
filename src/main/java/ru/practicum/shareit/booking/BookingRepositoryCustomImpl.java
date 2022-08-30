package ru.practicum.shareit.booking;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;

public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

    private final String request = "SELECT booking_id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "FROM items i " +
            "LEFT JOIN bookings b ON i.item_id = b.item_id " +
            "WHERE i.owner_id = ? ";
    private final String orderBy = "ORDER BY b.end_date DESC ";
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Booking> getByOwnerAndAllState(Long ownerId) {
        Query query = entityManager.createNativeQuery(request +
                "AND (b.status LIKE 'WAITING' OR b.status LIKE 'APPROVED') " +
                orderBy, Booking.class);
        query.setParameter(1, ownerId);
        return (List<Booking>) query.getResultList();
    }

    @Override
    public List<Booking> getByOwnerAndCurrentState(Long ownerId, LocalDateTime time) {
        Query query = entityManager.createNativeQuery(request +
                "AND (b.start_date < ? AND b.end_date > ?) " +
                orderBy, Booking.class);
        query.setParameter(1, ownerId);
        query.setParameter(2, time);
        query.setParameter(3, time);
        return (List<Booking>) query.getResultList();
    }

    @Override
    public List<Booking> getByOwnerAndPastState(Long ownerId, LocalDateTime time) {
        Query query = entityManager.createNativeQuery(request +
                "AND (b.status LIKE 'WAITING' OR b.status LIKE 'APPROVED') " +
                "AND b.end_date < ? " +
                orderBy, Booking.class);
        query.setParameter(1, ownerId);
        query.setParameter(2, time);
        return (List<Booking>) query.getResultList();
    }

    @Override
    public List<Booking> getByOwnerAndFutureState(Long ownerId, LocalDateTime time) {
        Query query = entityManager.createNativeQuery(request +
                "AND (b.status LIKE 'WAITING' OR b.status LIKE 'APPROVED') " +
                "AND b.start_date > ? " +
                orderBy, Booking.class);
        query.setParameter(1, ownerId);
        query.setParameter(2, time);
        return (List<Booking>) query.getResultList();
    }

    @Override
    public List<Booking> getByOwnerAndWaitingOrRejectedState(Long ownerId, BookingStatus status) {
        Query query = entityManager.createNativeQuery(request +
                "AND b.status LIKE ? " +
                orderBy, Booking.class);
        query.setParameter(1, ownerId);
        query.setParameter(2, status.toString());
        return (List<Booking>) query.getResultList();
    }

    @Override
    public Booking getLastBooking(Long ownerId, Long itemId, LocalDateTime time) {
        Query query = entityManager.createNativeQuery(request +
                "AND i.item_id = ? AND b.end_date  < ? " +
                orderBy +
                "LIMIT 1", Booking.class);
        query.setParameter(1, ownerId);
        query.setParameter(2, itemId);
        query.setParameter(3, time);
        Booking booking;
        try {
            booking = (Booking) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return booking;
    }

    @Override
    public Booking getNextBooking(Long ownerId, Long itemId, LocalDateTime time) {
        Query query = entityManager.createNativeQuery(request +
                "AND i.item_id = ? AND b.start_date > ? " +
                "ORDER BY b.start_date " +
                "LIMIT 1", Booking.class);
        query.setParameter(1, ownerId);
        query.setParameter(2, itemId);
        query.setParameter(3, time);
        Booking booking;
        try {
            booking = (Booking) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return booking;
    }
}