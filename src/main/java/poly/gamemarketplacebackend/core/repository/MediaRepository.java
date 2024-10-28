package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Media (media_name, media_url, sys_id_game) VALUES (:mediaName, :mediaUrl, :sysIdGame)", nativeQuery = true)
    void insertMedia(@Param("mediaName") String mediaName, @Param("mediaUrl") String mediaUrl, @Param("sysIdGame") Integer sysIdGame);

}