# NBE12-14-1-I-O

백엔드 12기 14회차 1차 프로젝트 I/O팀 레포지터리입니다.

## 일정

| 일 | 월 | 화 | 수 | 목 | 금 | 토 |
|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
| | 24 | 25 | 26 | 27<br>개발 마무리 | 28<br>멘토링 | 29 |
| 30 | 31 | | | | | |

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

## 협업 규칙

### 브랜치 전략

- 모든 작업 브랜치는 최신 `main` 브랜치에서 생성한다.
- `main` 브랜치에는 직접 push하지 않는다.
- 모든 변경 사항은 PR과 리뷰를 통해 `main`에 merge한다.

### PR 규칙

- 제목 규칙: `[타입] 작업내용`
- PR 본문은 PR Template에 맞춰 작성한다.
- 수정 사항과 중점 리뷰 포인트를 작성한다.
- 관련 Notion WBS 작업을 기재한다.
- 최소 1명의 리뷰 승인 후 `main`에 merge한다.

예시:

- `[feat] 주문 생성 API 구현`
- `[fix] 주문 총액 계산 오류 수정`
- `[docs] 협업 규칙 수정`

### Branch 규칙

명명 규칙:

`타입/작업명`

- 영문 소문자를 사용한다.
- 단어는 `-`로 구분한다.

예시:

- `feat/order-create`
- `feat/order-list`
- `fix/order-price`
- `chore/project-setting`

### Commit Message 규칙

형식:

`타입: 작업내용`

타입:

- `feat`: 새로운 기능
- `fix`: 버그 수정
- `refactor`: 기능 변경 없는 코드 개선
- `test`: 테스트 코드
- `docs`: 문서 수정
- `chore`: 설정 및 개발 환경 작업

예시:

- `feat: 주문 생성 API 구현`
- `fix: 주문 총액 계산 오류 수정`
- `refactor: 주문 생성 로직 분리`
- `test: 주문 생성 테스트 추가`
- `docs: README 협업 규칙 추가`
- `chore: H2 설정 추가`

---

## 작업 흐름

### 1. 새로운 작업 시작

새로운 기능 개발을 시작하기 전에 로컬 `main` 브랜치를 최신 상태로 갱신합니다.

```
git checkout main
git pull --ff-only origin main
```

- `git checkout main`
    - 현재 브랜치를 `main`으로 변경합니다.
- `git pull --ff-only origin main`
    - 원격 저장소의 최신 `main` 내용을 로컬 `main`에 반영합니다.
    - `--ff-only`를 사용하여 불필요한 merge commit이 생성되는 것을 방지합니다.

최신 `main`을 기준으로 새로운 작업 브랜치를 생성합니다.

```
git checkout -b feat/order-create
```

브랜치명은 다음 규칙을 따릅니다.

```
타입/작업명
```

예시:

```
feat/order-create
feat/order-list
feat/product-create
fix/order-price
chore/project-setting
```

---

### 2. 작업 중 최신 `main` 반영

기능을 개발하는 동안 다른 팀원의 PR이 `main`에 merge될 수 있습니다.

현재 작업 중인 브랜치에 최신 `main`의 변경 사항을 반영해야 하는 경우 다음과 같이 진행합니다.

먼저 원격 저장소의 최신 정보를 가져옵니다.

```
git fetch origin
```

`fetch`는 원격 저장소의 최신 정보를 가져오기만 하며, 현재 작업 중인 코드에는 바로 영향을 주지 않습니다.

그다음 최신 `main`을 현재 작업 브랜치에 합칩니다.

```
git merge origin/main
```

예를 들어 현재 브랜치가 `feat/order-create`라면,

```
git fetch origin
git merge origin/main
```

을 통해 최신 `main`의 변경 사항을 `feat/order-create`에 반영할 수 있습니다.