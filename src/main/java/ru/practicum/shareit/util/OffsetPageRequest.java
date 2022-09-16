package ru.practicum.shareit.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ValidationException;

public class OffsetPageRequest implements Pageable {

    private final Integer from;
    private final Integer size;
    private final Sort sort;

    public OffsetPageRequest(Integer from, Integer size, Sort sort) {
        if (size == null) {
            size = Integer.MAX_VALUE;
        }
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента не может быть отрицательным");
        }
        if (size < 1) {
            throw new ValidationException("Количество элементов на странице не может быть меньше одного");
        }
        this.size = size;
        this.from = from;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return from;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
