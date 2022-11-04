package com.gonggu.board.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -1351020554L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard board = new QBoard("board");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final BooleanPath deletion = createBoolean("deletion");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<BoardImage, QBoardImage> images = this.<BoardImage, QBoardImage>createList("images", BoardImage.class, QBoardImage.class, PathInits.DIRECT2);

    public final ListPath<BoardKeyword, QBoardKeyword> keywords = this.<BoardKeyword, QBoardKeyword>createList("keywords", BoardKeyword.class, QBoardKeyword.class, PathInits.DIRECT2);

    public final ListPath<BoardMember, QBoardMember> members = this.<BoardMember, QBoardMember>createList("members", BoardMember.class, QBoardMember.class, PathInits.DIRECT2);

    public final NumberPath<Integer> nowCount = createNumber("nowCount", Integer.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Integer> recruitmentNumber = createNumber("recruitmentNumber", Integer.class);

    public final StringPath title = createString("title");

    public final StringPath url = createString("url");

    public final QUser user;

    public final NumberPath<Integer> view = createNumber("view", Integer.class);

    public QBoard(String variable) {
        this(Board.class, forVariable(variable), INITS);
    }

    public QBoard(Path<? extends Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard(PathMetadata metadata, PathInits inits) {
        this(Board.class, metadata, inits);
    }

    public QBoard(Class<? extends Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

