package kz.nurdaulet.dao;

import java.util.List;

public interface FavoriteFoodDao {
    List<Long> findFoodIdsByUserId(Long userId);

    boolean existsByUserIdAndFoodId(Long userId, Long foodId);

    void save(Long userId, Long foodId);

    void delete(Long userId, Long foodId);
}
