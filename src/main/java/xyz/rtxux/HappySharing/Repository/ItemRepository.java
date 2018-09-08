package xyz.rtxux.HappySharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xyz.rtxux.HappySharing.Model.Item;
import xyz.rtxux.HappySharing.Model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    public List<Item> findAllByOwner(User owner);
    public List<Item> findAllByBorrower(User borrower);

    @Query(value = "SELECT * FROM item WHERE id IN (SELECT id FROM (SELECT id FROM item ORDER BY RAND() LIMIT ?1) t)", nativeQuery = true)
    List<Item> findRandomItems(Long quantity);
}
