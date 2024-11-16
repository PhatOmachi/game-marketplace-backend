package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.dto.CommentDTO;
import poly.gamemarketplacebackend.core.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentAPI {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        CommentDTO createdComment = commentService.createComment(commentDTO);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer id, @RequestBody CommentDTO commentDTO) {
        CommentDTO updatedComment = commentService.updateComment(id, commentDTO);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Integer id) {
        CommentDTO commentDTO = commentService.getCommentById(id);
        return ResponseEntity.ok(commentDTO);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        List<CommentDTO> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/p/game/{slug}/latest-comments")
    public ResponseEntity<List<CommentDTO>> getTop3LatestCommentsByGameSlug(@PathVariable String slug) {
        List<CommentDTO> comments = commentService.getTop3LatestCommentsByGameSlug(slug);
        return ResponseEntity.ok(comments);
    }
}
