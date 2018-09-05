package xyz.rtxux.HappySharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.rtxux.HappySharing.Model.ImageInfo;

@Repository
public interface ImageInfoRepository extends JpaRepository<ImageInfo, Long> {

}
