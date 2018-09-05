package xyz.rtxux.HappySharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.rtxux.HappySharing.Model.ImageDatabaseStorage;

@Repository
public interface ImageDatabaseStorageRepository extends JpaRepository<ImageDatabaseStorage, Long> {
}
