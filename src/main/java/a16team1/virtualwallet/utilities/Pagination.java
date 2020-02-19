package a16team1.virtualwallet.utilities;

import a16team1.virtualwallet.models.User;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

public class Pagination {
    public static <E> Page<E> pagedList(Pageable pageable, List<E> listOfEntities) {
        List<E> listToReturn = listOfEntities;
        if (pageable.isPaged()) {
            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber();
            int startItem = currentPage * pageSize;
            if (listOfEntities.size() < startItem) {
                listToReturn = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, listOfEntities.size());
                listToReturn = listOfEntities.subList(startItem, toIndex);
            }
        }
        return new PageImpl<>(listToReturn, pageable, listOfEntities.size());
    }

    public static <E> void addPagination(Pageable pageable, Query<E> query) {
        if (pageable.isPaged()) {
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            query.setFirstResult(pageNumber * pageSize).setMaxResults(pageSize);
        }
    }

}
