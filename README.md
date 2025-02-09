# 스프링 쇼핑몰 프로젝트

이메일: jb.lee159@gmail.com

프로젝트 페이지: https://app.appsmith.com/app/demo2/itemlist-671b34ce899642282b4bd5f4?embed=true

---
## 📙소개
- 프로젝트 소개
  - 등록된 상품 목록을 확인하고 주문할 수 있는 쇼핑몰 서비스입니다.
  - 프로젝트를 통해 개발의 전 과정을 경험해보고자 시작하게 되었습니다.

- 개발 기간: 2024.09 ~ 2024.11

- 인원: 1명

- 사용 기술: 
  - Java
  - Spring Boot, Spring Security, JPA, QueryDSL
  - Mysql, Redis
  - AWS(ec2, S3, RDS), Docker
  - postman

---
## 🛠아키텍처
![쇼핑몰아키텍처](https://github.com/user-attachments/assets/a0216181-ac3a-4711-91b2-c931630e8e1e)

---
## 🔧ERD 설계
![demo2](https://github.com/user-attachments/assets/bbbcdb9b-e9b2-4fbc-963c-c928805a9c05)


---
## 💥주요 문제 상황 및 해결

### 1. 쿼리 및 성능 개선

**문제 상황 및 원인:**

- 주문 관리 페이지에서 초기 주문 정보를 조회할 때 응답시간이 오래 걸린다는 것을 인지함

- 주문 테이블을 조회할 때, 각 주문에 해당하는 주문 상세 테이블을 별도로 조회하는 JPA의 N + 1 문제가 발생하고 있다는 것을 파악함


**문제 해결:**

- 주문 테이블을 조회할 때, 주문 상세 테이블을 조인하여 한 번의 쿼리로 가져오도록 수정하여 

- 데이터베이스 호출을 최소화하고 응답시간을 개선함 (187ms -> 117ms, 약 40% 개선)

<img src="https://github.com/user-attachments/assets/fa0af6d9-9ac4-4827-a59a-a68e256ddf85" alt="N+1 Join으로 해결한 예시 코드" width="500"/>

<br><br>

### 2. Refresh Token 탈취에 대한 보안 전략 적용

**문제 상황 및 원인:**

- Refresh Token을 사용하면서, 탈취될 경우 보안적으로 취약점이 발생할 수 있다는 점이 우려되었다. 

- Refresh Token은 유효기간이 길어, 탈취된다면 공격자가 긴 유효기간 동안 사용자의 자원에 접근할 수 있게 된다.


**문제 해결:**

- RTR(Refresh Token Rotation) 방식을 적용하여, Access Token이 만료될 때마다 Refresh Token도 함께 재발급하도록 했다.
  
- Refresh Token이 재발급되면 이전의 Refresh Token은 무효화되어 사용할 수 없다.
  
- 이 과정을 통해, 탈취된 Refresh Token을 사용할 수 없도록 하여 보안을 강화했다.

<br><br>

### 3. Redis를 활용한 일시적인 데이터 관리

**문제 상황 및 원인:**

- Refresh Token과 이메일 인증 코드는 한 번 사용하면 더 이상 사용되지 않아, RDB에 저장할 경우 계속해서 쌓이게 되어 공간 낭비가 발생한다는 문제를 파악했다.

**문제 해결:**

- Redis의 TTL(Time To Live) 기능을 활용하여, 유효기간이 지나면 자동으로 삭제도록 설정함으로써 불필요한 데이터가 쌓이지 않도록 관리하였다.

<br><br>

### 4. 퍼사드 패턴을 통한 의존성 문제 개선

**문제 상황 및 원인:**

- 하나의 컨트롤러가 여러 서비스를 호출하는 경우가 많았는데, 이렇게 많은 서비스에 의존하는 것이 괜찮은가에 대한 고민을 하게 되었다.

**문제 해결:**

- 퍼사드 패턴을 도입하여, 여러 서비스를 통합한 퍼사드 클래스들을 구현했다.
  
- 이를 통해 컨트롤러가 여러 서비스를 의존하는 대신, 하나의 퍼사드 또는 서비스만을 의존하도록 구조를 개선했다.
  
- 서비스 간의 직접적인 상호작용 또한 제거하여, 의존성을 관리함으로써 순환 참조 문제를 예방할 수 있었다.

<br><br>

---
## ⭐주요 기능
### 사용자

- **회원가입**: 회원가입을 위해 Gmail에서 제공하는 SMTP를 사용하여 인증 메일을 발송하고 redis에 저장, 일치하면 회원가입 버튼이 활성화
  
  인증번호는 일시적인 데이터이므로 redis의 TTL을 사용하여 5분 뒤 삭제되도록 설정
    
![인증번호발송](https://github.com/user-attachments/assets/d4154b89-7c9c-4868-aec0-741c41f929cd)
<img src="https://github.com/user-attachments/assets/86192392-86c9-4895-8750-2ba6395f5ef5" alt="사용자정보수정" width=500/><br><br><br>


- **로그인**: Spring Security + JWT를 이용한 인증 구현
  
  서버에 상태를 저장하지 않는 stateless 방식으로, 서버의 부담을 줄이고 확장성을 높이는 데 유리하다는 점에서 JWT를 선택함

![로그인+비밀번호초기화](https://github.com/user-attachments/assets/2c7a0582-3f88-4c65-85fb-d9c74691b4d2)


- **사용자 정보 수정**

<img src="https://github.com/user-attachments/assets/489e3527-bc27-4e19-abea-9e92e55fdd2f" alt="사용자정보수정" width=500/><br><br><br>



- **상품 목록 및 상품 상세**

![상품목록+상품상세](https://github.com/user-attachments/assets/b9f1eb71-e65d-4649-810a-79e37a82a7fc)


- **장바구니**: 장바구니에 담은 상품 목록, 장바구니 내에서 상품제거 및 수량변경 가능

<img src="https://github.com/user-attachments/assets/87cc61c6-ccff-4a15-bb3e-14c053a95836" alt="장바구니" width=500/><br><br><br>


- **주문 목록 및 후기 작성**: 사용자의 주문 목록, 주문완료 상태에 한해서 주문취소 가능, 배송완료 후 후기작성 가능

![주문목록+후기작성](https://github.com/user-attachments/assets/1219635f-78e3-47cf-aa40-2d1f288f3332)


### 관리자
- **상품 등록**: AWS S3를 사용하여 이미지를 저장하고, S3에 저장된 이미지 url을 상품 정보와 함께 데이터베이스에 저장

<img src="https://github.com/user-attachments/assets/f3b6455e-0944-47bc-8f7e-895b9ac48a95" alt="상품등록" width=500/><br><br><br>


- **상품 관리**: 등록된 상품들을 확인하고, 상품을 삭제할 수 있다.
  
<img src="https://github.com/user-attachments/assets/cac0a56b-dd56-4467-a8b1-3e95cec36c1c" alt="상품관리" width=500/><br><br><br>


- **주문 관리**: 주문 상태별로 주문 목록을 관리하고, 주문 상태를 변경할 수 있다.

<img src="https://github.com/user-attachments/assets/45c0644e-24da-4a0f-b341-cde71572e32d" alt="주문관리" width=500/><br><br><br>


- **후기 관리**: 사용자들이 작성한 후기를 확인하고, 부적절한 후기를 숨길 수 있다.

<img src="https://github.com/user-attachments/assets/990844d4-444a-45af-b1bb-6e94a948c436" alt="후기관리" width=500/><br><br><br>


- **사용자 관리**: 사용자들의 정보를 확인할 수 있고, 특정 사용자를 차단할 수 있다.

<img src="https://github.com/user-attachments/assets/4d4ce6af-5709-4f27-88a8-f095b60f027d" alt="사용자관리" width=500/><br><br><br>


