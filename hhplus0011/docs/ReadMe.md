## 📌마일스톤

👉🏻 <a href="https://github.com/users/devNana222/projects/1/views/1">마일스톤 github</a>

## 📌API 명세서

👉🏻  <a href="https://documenter.getpostman.com/view/34151728/2sAXxQdX3T">Postman API 명세서</a>


## 📌 ERD & Table
<details>
  <summary>ERD</summary>
<div>

  ![img._erd.png](img_erd.png)

</div>
</details>

<details>
<summary>DB Query</summary>
<div>

```mysql

CREATE TABLE customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT,
    balance INT NOT NULL,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    edit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE product (
product_id INT PRIMARY KEY AUTO_INCREMENT,
product_nm VARCHAR(255) NOT NULL,
price INT NOT NULL,
reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
edit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE product_inventory (
id INT PRIMARY KEY AUTO_INCREMENT,
product_id INT NOT NULL,
amount INT NOT NULL,
edit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `order` (
order_id INT PRIMARY KEY AUTO_INCREMENT,
customer_id INT NOT NULL,
order_total_price INT NOT NULL,
reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
edit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_product (
id INT PRIMARY KEY AUTO_INCREMENT,
order_id INT NOT NULL,
product_id INT NOT NULL,
amount INT NOT NULL,
price INT NOT NULL,
reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart (
cart_id INT PRIMARY KEY AUTO_INCREMENT,
product_id INT NOT NULL,
amount INT NOT NULL,
reg_id VARCHAR(255),
edit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

```

</div>
</details>


## 📌시스템 요구사항 명세서
<details>
  <summary>시스템 요구사항 명세서</summary>
<div>

## 1. 개요
### 1.1 목적
본 문서는 E-Commerce 상품 주문서비스의 요구사항을 명세합니다.

### 1.2 범위
E-Commerce System은 다음의 기능을 제공합니다.
* 고객 포인트 충전 / 사용
* 제품 검색 / 관리
* 최근3일간 판매 top5 검색
* 결제 및 주문 처리
* 장바구니 관리

### 1.3 용어 정의 및 약어
* 고객 : 제품을 구매하는 사용자.
* 장바구니 : 고객이 관심있는 물품을 임시로 담아두는 가상 공간.
* 결제 : 고객이 제품을 구매함. 단, 결제수단은 충전된 포인트를 사용하며 결제는 외부 시스템에 의해 이루어진다.
* API: Application Programming Interface
* SRS: System Requirements Specification


## 2. 시스템 개요
### 2.1 제품 기능
이 시스템은 다음과 같은 주요 기능을 제공한다.
1. 포인트 충전 / 조회

2. 상품 조회

3. 주문 / 결제

4. 상위 상품 조회

5. 장바구니에 상품 추가 / 삭제 / 조회

### 2.2 사용자 특성
* 제품을 구매하고 검색하는 사용자.

### 2.3 운영 환경
* 웹 브라우저 : Chrome, Firefox, Edge, Safari등의 최신 버전
* Web Application Server
    * java 21
    * Spring Boot 3.3.4
    * Spring Data JPA
    * Query DSL
    * JWT
* Messaging Solution
    * Spring Kafka
* DB
    * MySQL 8.0
    * Redis
* Documentation
    * Postman
* 결제 게이트웨이
    * 외부 결제 서비스 Mock API로 대체

## 3. 요구사항
### 3.1 기능적 요구사항
### 3.1.1 잔액 충전 / 조회 API
* 고객 번호와 충전할 포인트를 입력하여 포인트를 충전할 수 있다.
* 충전한 포인트는 고객 번호로 조회할 수 있다.
### 3.1.2 상품 조회 API
* 고객은 상품 정보(ID, 이름, 가격, 잔여수량)를 조회할 수 있다.
### 3.1.3 주문 / 결제
*  고객은 상품 ID와 수량 목록을 입력받아 주문할 수 있다.
*  결제는 기 충전된 잔액을 기반으로 이루어지며 주문할 시 잔액을 차감한다.
*  데이터 분석을 위해 결제 성공 시에 실시간으로 주문 정보를 데이터 플랫폼에 전송해야한다.
*  데이터 플랫폼은 '외부'이며 본 시스템에서는 Mock API, Fake Module등의 방법으로 전송한다.

### 3.1.4 상위 상품 조회
* 최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공한다.

### 3.1.5 장바구니에 상품 추가 / 삭제 / 조회
* 고객은 구매 이전에 관심있는 상품들을 장바구니에 적재할 수 있다.

### 3.2 성능 요구사항
* 동시에 여러 주문이 들어올 경우 유저의 보유 잔고에 대한 처리가 정확해야한다.
* 각 상품의 재고 관리가 정상적으로 이루어져 잘못된 주문이 발생하지 않아야한다.
* 상품조회 시 조회 시점의 상품별 잔여수량이 정확해야한다.
### 4. 데이터 요구사항
### 4.1 데이터베이스 구조
* 상품 테이블 : 상품ID, 상품명, 가격, 등록일, 수정일
* 상품 재고 테이블 : ID, 상품 ID, 잔여 수량, 수정일
* 주문 테이블 : 주문ID, 고객ID, 주문 총 금액, 주문일, 수정일
* 주문 상품 테이블 : ID, 주문ID, 상품ID, 주문 수량, 주문 가격, 등록일
* 고객 테이블 : 고객ID, 장바구니 ID, 잔여 point, 등록일, 수정일
* 장바구니 테이블 : 장바구니 ID, 상품ID, 수량, 등록일, 수정일
</div>

</details>

# 📌패키지 구조
<details>
<summary>패키지 구조</summary>
<div>

## layered Architecture 
본 프로젝트는 서비스 별로 패키지를 나눈 *레이어드 아키텍처* 를 따르고 있습니다.<br/>
구조는 필요에의해 유연하게 변경할 계획이며 서비스간, 레이어간의 데이터 의존성을 최소화 하는 것이 목적입니다. 

```html
    ├─main
    │  ├─java
    │  │  └─com
    │  │      └─tdd
    │  │          └─ecommerce
    │  │              ├─balance
    │  │              │  ├─controller
    │  │              │  ├─domain
    │  │              │  ├─dto
    │  │              │  ├─repository
    │  │              │  └─service
    │  │              ├─cart
    │  │              │  ├─controller
    │  │              │  ├─domain
    │  │              │  ├─dto
    │  │              │  ├─repository
    │  │              │  └─service
    │  │              ├─order
    │  │              │  ├─controller
    │  │              │  ├─domain
    │  │              │  ├─dto
    │  │              │  ├─repository
    │  │              │  └─service
    │  │              └─product
    │  │                  ├─controller
    │  │                  ├─domain
    │  │                  ├─dto
    │  │                  ├─repository
    │  │                  └─service

```

</div>
</details>