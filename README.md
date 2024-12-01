# 스프링 쇼핑몰 프로젝트
기존 git respository에 yml파일이 올라가있는 것을 확인하여 수정했지만 여전히 커밋에 남아있어 새로운 repository에 업로드하게 되었습니다.

<img src="https://github.com/user-attachments/assets/91a4aeac-2d25-4b63-80db-f6df11ff6e40" alt="이전 repository의 commit" width=500/>

이메일: jb.lee159@gmail.com

프로젝트 페이지: https://app.appsmith.com/app/demo2/itemlist-671b34ce899642282b4bd5f4?embed=true

---
## 📙소개
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

---
## 🛠아키텍처
![쇼핑몰프로젝트아키텍처](https://github.com/user-attachments/assets/d9bfa0b0-072b-41ff-86ef-5faf5ad5bf8e)

---
## 🔧ERD 설계
![demo2](https://github.com/user-attachments/assets/bbbcdb9b-e9b2-4fbc-963c-c928805a9c05)

---
## ⭐주요 기능
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

---
## 트러블 슈팅
문제: 

주문 관리 페이지에서 초기 주문 정보를 조회할 때 응답시간이 오래 걸린다는 것을 인지함

원인:

주문 테이블을 조회할 때, 각 주문에 해당하는 주문 상세 테이블을 별도로 조회하는 N + 1 문제가 발생하고 있다는 것을 파악함

문제 해결:

주문 테이블을 조회할 때, 주문 상세 테이블을 조인하여 한 번의 쿼리로 가져오도록 수정하여 데이터베이스 호출을 최소화하고 응답시간을 개선함

(187ms -> 117ms, 약 40% 개선)

<img src="https://github.com/user-attachments/assets/fa0af6d9-9ac4-4827-a59a-a68e256ddf85" alt="N+1 Join으로 해결한 예시 코드" width="300"/>
