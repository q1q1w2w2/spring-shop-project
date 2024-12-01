# 스프링 쇼핑몰 프로젝트
기존 git respository에 yml파일이 올라가있는 것을 확인하여 수정했지만 여전히 커밋에 남아있어 새로운 repository에 업로드하게 되었습니다.

프로젝트 페이지: https://app.appsmith.com/app/demo2/itemlist-671b34ce899642282b4bd5f4?embed=true

## 소개
- 프로젝트 소개
  - 등록된 상품 목록을 확인하고 주문할 수 있는 쇼핑몰 서비스입니다.
  - 프로젝트를 통해 개발의 전 과정을 경험해보고자 시작하게 되었습니다.

- 개발 기간: 2024.09.21 ~ 2024.11.10

- 인원: 1명

- 사용 기술: 
  - Java
  - Spring Boot, Spring Security, JPA, QueryDSL
  - Mysql, redis
  - AWS(ec2, S3, RDS), Docker
  - postman

- 실제 화면
  
![쇼핑몰소개화면](https://github.com/user-attachments/assets/a7d7b817-dd67-4aa6-aa9e-250de1b6bf51)


## 아키텍처
![쇼핑몰프로젝트아키텍처](https://github.com/user-attachments/assets/d9bfa0b0-072b-41ff-86ef-5faf5ad5bf8e)

## ERD 설계
![demo2](https://github.com/user-attachments/assets/8c1e6a38-0639-44a0-b6e1-f00ed17a4c88)

## 기능 소개
### 사용자
- **회원가입**: 회원가입을 위해 인증번호를 발송하고, 일치하면 회원가입 버튼이 활성화, (SMTP 설명)
    
![인증번호발송](https://github.com/user-attachments/assets/d4154b89-7c9c-4868-aec0-741c41f929cd)
![인증번호](https://github.com/user-attachments/assets/86192392-86c9-4895-8750-2ba6395f5ef5)

- **로그인**: JWT를 이용한 인증 구현

![로그인+비밀번호초기화](https://github.com/user-attachments/assets/2c7a0582-3f88-4c65-85fb-d9c74691b4d2)

- **사용자 정보 수정**

![정보수정](https://github.com/user-attachments/assets/489e3527-bc27-4e19-abea-9e92e55fdd2f)

- **상품 목록 및 상품 상세**

![상품목록+상품상세](https://github.com/user-attachments/assets/b9f1eb71-e65d-4649-810a-79e37a82a7fc)

- **장바구니**: 장바구니에 담은 상품 목록, 장바구니 내에서 상품제거 및 수량변경 가능

![장바구니](https://github.com/user-attachments/assets/87cc61c6-ccff-4a15-bb3e-14c053a95836)

- **주문 목록 및 후기 작성**: 사용자의 주문 목록, 주문완료 상태에 한해서 주문취소 가능, 배송완료 후 후기작성 가능

![주문목록+후기작성](https://github.com/user-attachments/assets/1219635f-78e3-47cf-aa40-2d1f288f3332)


### 관리자
- **상품 등록**

![상품등록](https://github.com/user-attachments/assets/f3b6455e-0944-47bc-8f7e-895b9ac48a95)

- **상품 관리**
  
![상품관리](https://github.com/user-attachments/assets/cac0a56b-dd56-4467-a8b1-3e95cec36c1c)

- **주문 관리**

![주문관리](https://github.com/user-attachments/assets/45c0644e-24da-4a0f-b341-cde71572e32d)

- **후기 관리**

![후기관리](https://github.com/user-attachments/assets/990844d4-444a-45af-b1bb-6e94a948c436)

- **사용자 관리**

![사용자관리](https://github.com/user-attachments/assets/4d4ce6af-5709-4f27-88a8-f095b60f027d)



