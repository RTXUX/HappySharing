package xyz.rtxux.HappySharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.rtxux.HappySharing.Model.Item;
import xyz.rtxux.HappySharing.Model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    public List<Item> findAllByOwner(User owner);
    public List<Item> findAllByBorrower(User borrower);
}
