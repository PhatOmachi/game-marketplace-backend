package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Media;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Media (media_name, media_url, sys_id_game) VALUES (:mediaName, :mediaUrl, :sysIdGame)", nativeQuery = true)
    void insertMedia(@Param("mediaName") String mediaName, @Param("mediaUrl") String mediaUrl, @Param("sysIdGame") Integer sysIdGame);

    @Modifying
    @Transactional
    @Query("DELETE FROM Media m WHERE m.game.sysIdGame = :sysIdGame")
    int deleteMediaByGameId(@Param("sysIdGame") Integer sysIdGame);

    @Modifying
    @Transactional
    @Query("DELETE FROM Media m WHERE m.mediaUrl = :mediaUrl")
    int deleteMediaByUrl(@Param("mediaUrl") String mediaUrl);

    @Query("SELECT m FROM Media m WHERE m.game.sysIdGame = :sysIdGame")
    List<Media> getMediaByGameId(@Param("sysIdGame") Integer sysIdGame);

}