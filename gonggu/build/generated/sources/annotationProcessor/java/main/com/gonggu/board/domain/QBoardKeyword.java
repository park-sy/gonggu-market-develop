package com.gonggu.board.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardKeyword is a Querydsl query type for BoardKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardKeyword extends EntityPathBase<BoardKeyword> {

    private static final long serialVersionUID = -585702125L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardKeyword boardKeyword = new QBoardKeyword("boardKeyword");

    public final QBoard board;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath keyword = createString("keyword");

    public QBoardKeyword(String variable) {
        this(BoardKeyword.class, forVariable(variable), INITS);
    }

    public QBoardKeyword(Path<? extends BoardKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardKeyword(PathMetadata metadata, PathInits inits) {
        this(BoardKeyword.class, metadata, inits);
    }

    public QBoardKeyword(Class<? extends BoardKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new QBoard(forProperty("board"), inits.get("board")) : null;
    }

}

