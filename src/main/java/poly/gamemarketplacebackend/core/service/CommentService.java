package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO);
    CommentDTO updateComment(Integer id, CommentDTO commentDTO);
    void deleteComment(Integer id);
    CommentDTO getCommentById(Integer id);
    List<CommentDTO> getAllComments();
    List<CommentDTO> getTop3LatestCommentsByGameSlug(String slug);
}