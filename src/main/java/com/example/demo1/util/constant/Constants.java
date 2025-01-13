package com.example.demo1.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    // 리뷰 작성 여부
    public static final int REVIEW_CANCEL = 0;
    public static final int REVIEW_COMP = 1;

    // 리뷰 차단
    public static final int REVIEW_BLIND_CANCEL = 0;
    public static final int REVIEW_BLIND = 1;

    // 삭제된 Item
    public static final int DELETED_ITEM_STATE = 1;

    // 삭제된 ItemImage
    public static final int DELETED_IMAGE = 0;

    // 유저 벤
    public static final int USER_BAN = 1;
    public static final int USER_UNBAN = 0;
}
