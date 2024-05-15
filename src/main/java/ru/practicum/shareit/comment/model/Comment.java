package ru.practicum.shareit.comment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "COMMENT")
    private String text;
    @ManyToOne
    private Item item;
    @ManyToOne
    private User user;
    private LocalDateTime created;
    @Column(name = "AUTHOR_NAME")
    private String authorName;
}
