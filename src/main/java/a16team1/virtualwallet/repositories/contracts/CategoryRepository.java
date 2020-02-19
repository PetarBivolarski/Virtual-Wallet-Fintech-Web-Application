package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.Category;
import a16team1.virtualwallet.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepository {

    Page<Category> getAll(Pageable pageable, User user);

    Category getById (int id);

    Category create(Category category);

    Category update(Category category);

    Category delete(Category category);

    boolean exists (int id);

}
