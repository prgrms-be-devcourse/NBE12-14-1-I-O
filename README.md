# NBE12-14-1-I-O

# ☕️ 아요 Coffee

매일 오후 2시, 오늘의 커피를 준비합니다.

## 📝 프로젝트 개요

‘아요 Coffee’는 스프링 부트 기반의 웹사이트입니다.
비회원 고객이 이메일로 원두를 주문할 수 있으며,
매일 전날 오후 2시부터 당일 오후 2시까지 받은 주문들을 하나의 배송 단위로 묶어 처리하는 원두 주문·배송 서비스를 제공합니다.
상품 조회, 주문·배송 처리, 관리자 상품 관리 및 통계 조회, 이메일을 통한 주문 내역 확인 등의 CRUD를 구현했습니다.

---

## 🏗️ 시스템 아키텍처 (System Architecture)

본 프로젝트는 Next.js 기반의 프론트엔드와 Spring Boot 기반의 백엔드가 분리된 멀티 모듈 구조로 설계되었습니다.
컴포넌트 구조와 네트워크 인프라 배치는 다음과 같습니다.

![시스템 아키텍처 다이어그램](https://github.com/user-attachments/assets/2e32ba8f-7b60-4769-be8c-5eec697139aa)

---

## 📊 ERD (Entity Relationship Diagram)

프로젝트의 핵심 도메인(상품, 주문, 배송) 간의 RDB 설계 구조입니다.

![아요 Coffee ERD](https://github.com/user-attachments/assets/b7cc46d6-350c-4a3f-8f00-40f468f8bf0d)

### 💡 핵심 설계 포인트
- **오후 2시 묶음 배송을 위한 DELIVERY 중심 설계:** 매일 전날 오후 2시부터 당일 오후 2시까지의 주문을 하나의 배송 단위로 처리하는 서비스 특성을 반영했습니다.
- **비회원 배송지 및 연락처 통합:** '배송 묶음 행위'의 주체가 되는 `DELIVERY` 테이블이 비회원 고유 식별 및 알림용 정보(`email`)와 수령지 정보(`address`, `postal_code`)를 관리하도록 설계했습니다.
- **배송과 주문의 1:N 구조:** 하나의 배송 단위(`DELIVERY`)가 다수의 개별 주문(`ORDER`)을 유기적으로 묶어 처리할 수 있도록 명확한 1:N 관계를 확립했습니다.

---

## 🛠️ 기술 스택 (Tech Stack)

### 백엔드 (Backend)
- **Java 25** (주 언어)
- **Spring Boot** (v4.1.1)
- **Spring Data JPA**
- **H2 Database** (인메모리 DB)
- **Lombok** / **Validation**

### 프론트엔드 (Frontend)
- **TypeScript** (주 언어)
- **Next.js** (v16.3.3)
- **React** (v19.2.8)
- **Tailwind CSS** (v4)
- **React Compiler**

### API 문서화 (API Documentation)
- **Swagger / Springdoc-openapi** (v3.1.0)

### 빌드 도구 (Build Tool)
- **Gradle** (Groovy DSL)

---

## 🔌 API 명세 (API Documentation)

프론트엔드와 백엔드의 유기적인 데이터 흐름을 위해 **Swagger(Springdoc-openapi)**를 도입하여 API를 공유하고 검증했습니다.
로컬 환경에서 서버 실행 시 아래 주소를 통해 전체 API 명세 확인 및 테스트가 가능합니다.

- **Swagger UI 주소:** `http://localhost:8080/api/v1/swagger-ui/index.html`

![Swagger 주문 API 명세 화면](https://github.com/user-attachments/assets/9af61d80-d727-4cae-a6b8-54278cf3843f)
![Swagger 관리자 API 명세 화면](https://github.com/user-attachments/assets/72e50996-71cd-4371-b35b-a8ed4adc3150)

---

## 📂 프로젝트 구조 (Project Structure)

루트 폴더를 기준으로 프론트엔드와 백엔드가 분리되어 유기적으로 관리되는 멀티 모듈 구조입니다.

```text
NBE12-14-1-I-O/                      # 루트 폴더
│
├── project/                        # 백엔드 영역 (Spring Boot 4.1.1)
│   └── src/main/java/io/project/
│       ├── domain/                 # 비즈니스 핵심 도메인 (배달, 주문, 상품)
│       │   ├── delivery/           # 배달 관리 레이어
│       │   ├── order/              # 주문/결제 API (OrderController, OrderAdminController)
│       │   └── product/            # 상품 CRUD API (ProductController, AdminProductController)
│       └── global/                 # 글로벌 공통 설정
│           ├── exception/          # 공통 예외 처리 (GlobalExceptionHandler)
│           └── scheduling/         # 관리자 대시보드 통계 자동 집계 스케줄러 (DeliveryScheduler)
│
└── frontend/                       # 프론트엔드 영역 (Next.js 16 & React 19)
    └── src/
        ├── app/                    # 서비스 페이지 및 라우팅 (App Router)
        │   ├── admin/dashboard/    # 관리자 통계 대시보드 페이지
        │   ├── cart/ & checkout/   # 장바구니 및 주문 결제 페이지
        │   └── orders/[orderId]/   # 주문 내역 및 상세 조회 페이지 (동적 라우팅)
        └── components/             # 독립형 UI 컴포넌트 (상품 추가/수정 모달, 카드 등)
```

---

## 팀원 및 역할
|    이름    |               역할               |
|:--------:|:------------------------------:|
| 백혜승 [팀장] |            주문 생성 기능            |
|   김민주    |       주문 수정 기능, 주문 삭제 기능       | 
|   김상범    |    주문 목록 조회 기능, 주문 상세 조회 기능    |
|   박현호    |    상품 추가 기능 (관리자), 상품 조회 기능    |
|   신시원    | 상품 수정 기능 (관리자), 상품 삭제 기능 (관리자) |

---

## 🚀 시작하기 (Getting Started)

로컬 개발 환경에서 프로젝트를 검증하기 위한 실행 방법입니다.

### 백엔드 실행 방법 (Backend Execution)
```bash
cd project
./gradlew bootRun
```
* 서버 실행 후 **Swagger API 문서**는 `http://localhost:8080/api/v1/swagger-ui/index.html`에서 확인하실 수 있습니다.

### 프론트엔드 실행 방법 (Frontend Execution)
```bash
cd frontend
npm install
npm run dev
```
* 브라우저에서 `http://localhost:3000`으로 접속하시면 **프론트엔드 개발 서버**를 확인하실 수 있습니다.
