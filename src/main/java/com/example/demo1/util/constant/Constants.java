package com.example.demo1.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // 인스턴스화 방지
public final class Constants {

    // 주문 상태
    public static final int ORDER_STEP_START = 2;
    public static final int ORDER_STEP_COMP = 3;
    public static final int ORDER_STEP_CANCEL = 10;

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

    // 장바구니 상태
    public static final int CART_ADD = 0; // 기본 상태
    public static final int CART_DELETED = 1; // 삭제됨
    public static final int CART_COMP = 2; // 주문됨

    // 유저 벤
    public static final int USER_BAN = 1;
    public static final int USER_UNBAN = 0;

    // 이메일 인증 코드
    public static final int EMAIL_CODE_ABLE = 1;
    public static final int EMAIL_CODE_DISABLE = 0;
}
