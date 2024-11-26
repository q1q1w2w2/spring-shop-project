package com.example.demo1.libs.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {
    // 직렬화된 객체와 역질렬화할 클래스가 같은 클래스인지 식별하기 위해 사용
    /*
    예를 들어 변경 후 새로운 변수가 추가되었지만 serialVersionUID 가 같을 경우
    새로 생긴 변수의 값이 정해지지 않는 경우 기본값으로 설정된다.
     */
    private static final long serialVersionUID = 1L;

    private Long idx;
    private String name;
    private String id;
    private String role;

    @Builder
    public TokenRequest(Long idx, String name, String id, String role){
        this.idx = idx;
        this.name = name;
        this.id = id;
        this.role = role;
    }

    // 객체를 문자열로 변환힌디.
    public String toString() {
        return "{" +
                "idx="+idx+", " +
                "name="+name+", " +
                "id="+id+", "+
                "role="+role+
                "}";
    }

    // toString() 메서드를 통해 변환된 문자열을 객체로 반환한다.
    public static TokenRequest toObject(String serializationString){
        String replaceString = serializationString.replace("{", "").replace("}","");
        String[] stringArray  = replaceString.split(", ");

        Long idx = null;
        String name = null;
        String id = null;
        String role = null;

        for(String s : stringArray){
            String[] splitArray = s.split("=");

            switch (splitArray[0]){
                case "idx":
                    idx = Long.parseLong(splitArray[1]);
                    break;
                case "name":
                    name = splitArray[1];
                    break;
                case "id":
                    id = splitArray[1];
                    break;
                case "role":
                    role = splitArray[1];
                    break;
            }
        }
        return TokenRequest.builder()
                .idx(idx)
                .name(name)
                .id(id)
                .role(role)
                .build();
    }
}
