package com.example.demo1.repository.item;

import com.example.demo1.entity.item.ItemCart;
import com.example.demo1.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCartRepository extends JpaRepository<ItemCart, Long> {

    @Query("select ic from ItemCart ic where ic.user = :user and ic.status = :status")
    Optional<List<ItemCart>> findByUserAndStatusIsLike(@Param("user") User user, @Param("status") int status);

    @Query("SELECT ic FROM ItemCart ic WHERE ic.user.id = :userIdx AND ic.item.idx = :itemIdx AND ic.status = 0")
    Optional<ItemCart> findByUserIdAndItemIdxAndStatusZero(@Param("userIdx") Long userIdx, @Param("itemIdx") Long itemIdx);
}
