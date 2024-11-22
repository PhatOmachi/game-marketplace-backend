package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.CommentDTO;
import poly.gamemarketplacebackend.core.entity.Comment;
import poly.gamemarketplacebackend.core.entity.Users;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.CommentMapper;
import poly.gamemarketplacebackend.core.mapper.UsersMapper;
import poly.gamemarketplacebackend.core.repository.CommentRepository;
import poly.gamemarketplacebackend.core.service.CommentService;
import poly.gamemarketplacebackend.core.service.UsersService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UsersMapper usersMapper;
    private final UsersService usersService;

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        Users currentUser = usersMapper.toEntity(usersService.getCurrentUser());
        validateCommentOnInsert(commentDTO, currentUser);
        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setUser(currentUser);
        comment = commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    private void validateCommentOnInsert(CommentDTO commentDTO, Users currentUser) {
        boolean isOwned = currentUser.getOwnedGames().stream()
                .anyMatch(game -> game.getGame().getSysIdGame().equals(commentDTO.getGameId()));
        if (!isOwned) {
            throw new CustomException("User does not own this game", HttpStatus.NOT_ACCEPTABLE);
        }
        boolean hasCommented = commentRepository.existsByUser_SysIdUserAndGame_SysIdGame(currentUser.getSysIdUser(), commentDTO.getGameId());
        if (hasCommented) {
            throw new CustomException("User has already commented on this game", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public CommentDTO updateComment(Integer id, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentMapper.updateEntityFromDTO(commentDTO, comment);
        comment = commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    @Override
    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }

    @Override
    public CommentDTO getCommentById(Integer id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentMapper.toDTO(comment);
    }

    @Override
    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getTop3LatestCommentsByGameSlug(String slug) {
        List<Comment> comments = commentRepository.findTop3ByGameSlugOrderByCommentDateDesc(slug);
        return comments.stream().limit(3)
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getCommentByUsername(String username) {
        List<Comment> comments = commentRepository.findByUserUsername(username);
        return comments.stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> findCommentWithDesAndDateRange(String username, String des, LocalDate startDate, LocalDate endDate) {
        List<Comment> comments = commentRepository.findByUserUsername(username);
        Predicate<Comment> filterPredicate = comment -> true;

        if(des != null){
            filterPredicate = filterPredicate.and(comment -> comment.getContext().contains(des));
        }

        if(startDate != null){
            filterPredicate = filterPredicate.and(comment -> !comment.getCommentDate().toLocalDate().isBefore(startDate));
        }

        if(endDate != null){
            filterPredicate = filterPredicate.and(comment -> !comment.getCommentDate().toLocalDate().isAfter(endDate));
        }

        return comments.stream()
                .filter(filterPredicate)
                .map(comment -> commentMapper.toDTO(comment))
                .collect(Collectors.toList());
    }
}