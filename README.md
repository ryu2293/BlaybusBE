# BlaybusBE

# 🎓 Blavus (블레이버스)

> 멘토의 관리 공수는 줄이고, 멘티의 학습 데이터는 정교하게 쌓는 스마트 학습 관리 플랫폼
> ![스크린샷 2026-04-11 오전 3.50.34.png](attachment:54e9651e-bce4-4ca1-b09b-0b9d84dd092c:스크린샷_2026-04-11_오전_3.50.34.png)

<br>

## 📌 프로젝트 소개

멘토링 현장의 비효율적인 과제 전달 방식과 불투명한 성취도 관리를 해결하기 위한 MVP 단계의 웹/모바일 서비스입니다.

- **멘토**: 반복 업무 자동화로 관리 공수 최소화
- **멘티**: 정교한 데이터 기반의 성취도 확인 및 학습 관리

<br>

## ⚙️ 개발 기간 및 팀 구성

| 항목 | 내용 |
|---|---|
| 개발 기간 | 2025.02.02 ~ 2025.02.13 |
| 팀 구성 | 7인 (PM-2, FE-1, BE-2, UIUX-2) |
| 수상 | 🏆 팀워크상 |

<br>

## 🛠 Stack & Library

**Frontend**

![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat&logo=typescript&logoColor=white)
![React](https://img.shields.io/badge/React-61DAFB?style=flat&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=white)

**Backend**

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring JPA](https://img.shields.io/badge/Spring_JPA-6DB33F?style=flat&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)

**Infra**

![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat&logo=amazonec2&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=flat&logo=amazonrds&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=flat&logo=amazons3&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=githubactions&logoColor=white)

**알림**

![FCM](https://img.shields.io/badge/FCM-FFCA28?style=flat&logo=firebase&logoColor=black)

<br>

## ✨ 주요 기능

### 1. 과제 및 일정 생성 (멘토)
- 과목, 요일, 반복 주기를 설정하여 멘티에게 과제 및 일정 일괄 생성
- PDF 학습지 첨부 가능
- 반복 일정 지원으로 매주 동일한 과제 자동 등록

### 2. 월간 캘린더
- 과목별 색상으로 구분된 월간 캘린더로 전체 일정 한눈에 확인
- 멘토는 담당 멘티의 과제 현황, 멘티는 본인 학습 일정 조회

### 3. 이미지 좌표 기반 피드백
- 멘티가 제출한 과제 이미지의 특정 위치를 클릭하여 위치 지정 피드백 작성
- 비율 좌표 방식으로 해상도에 무관하게 정확한 위치에 피드백 표시
- 피드백 등록 시 FCM 알림으로 멘티에게 즉시 알림 발송

<br>

## 🏗 아키텍처

![Architecture](![설스터디 아키텍처.png](attachment:282a8f2a-52a9-4518-9256-4bae56526cc9:설스터디_아키텍처.png))

<br>

## 🚀 트러블슈팅

### 핫픽스 배포 경험

**문제**
발표 직전, 피드백 작성 시 300자 이상 입력 시 DB 컬럼 사이즈 제한으로 서버 오류 발생.
프론트엔드에서 별도의 글자 수 제한 처리가 없어 300자 초과 입력이 가능한 상태였음.

**해결**
EC2 로그 분석으로 원인 파악 → RDS 컬럼 사이즈 즉시 수정 → 운영 환경 핫픽스 배포로 장애 복구

**교훈**
기획 단계의 정밀한 ERD 설계와 엣지 케이스(Edge Case) 테스트의 중요성 체감

<br>

## 💬 회고

**배운 점**
- 단기 해커톤(12일) 내 대량의 핵심 기능 구현 및 AWS 배포 환경 직접 구축 경험
- 운영 중 장애 상황에서 EC2 로그 분석 → DB 수정 → 핫픽스 배포까지 실제 트러블슈팅 경험

**아쉬운 점**
- 심사평에서 "구조적 깊이가 아쉽다"는 피드백을 받아 성능 최적화 및 확장성 고려의 필요성 인식
