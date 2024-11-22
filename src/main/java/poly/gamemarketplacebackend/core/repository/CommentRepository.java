package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.game.slug = :slug ORDER BY c.commentDate DESC")
    List<Comment> findTop3ByGameSlugOrderByCommentDateDesc(@Param("slug") String slug);

    boolean existsByUser_SysIdUserAndGame_SysIdGame(Integer userId, Integer gameId);

    List<Comment> findByUserUsername(String username);
}