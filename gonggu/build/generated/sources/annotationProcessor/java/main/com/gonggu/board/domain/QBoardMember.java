package com.gonggu.board.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardMember is a Querydsl query type for BoardMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardMember extends EntityPathBase<BoardMember> {

    private static final long serialVersionUID = 1284912688L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardMember boardMember = new QBoardMember("boardMember");

    public final QBoard board;

    public final BooleanPath host = createBoolean("host");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final QUser user;

    public QBoardMember(String variable) {
        this(BoardMember.class, forVariable(variable), INITS);
    }

    public QBoardMember(Path<? extends BoardMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardMember(PathMetadata metadata, PathInits inits) {
        this(BoardMember.class, metadata, inits);
    }

    public QBoardMember(Class<? extends BoardMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new QBoard(forProperty("board"), inits.get("board")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

