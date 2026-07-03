# PROJECT_STRUCTURE.md

本文件說明 Chinese Restaurant Ordering System 的主要專案結構與分層責任。

---

## 1. 架構原則

本專案採用：

```text
MVC + DAO Pattern
```

目標是讓 UI、商業邏輯與資料庫存取分離，方便維護與擴充。

---

## 2. 主要資料夾

```text
src/main/java
├── controller
├── dao
├── dao.impl
├── exception
├── model
├── service
├── service.impl
└── util
```

---

## 3. controller

負責 Swing UI 畫面與使用者事件。

主要檔案：

| 檔案 | 說明 |
|---|---|
| LoginUI.java | 登入畫面 |
| RegisterUI.java | 註冊畫面 |
| MainUI.java | 主畫面與左側選單 |
| MemberUI.java | 會員管理 |
| ProductUI.java | 商品管理 |
| ShoppingUI.java | 點餐與結帳 |
| OrderUI.java | 訂單查詢與重印 |

原則：

- 只處理 UI 顯示與事件。
- 不直接撰寫 SQL。
- 透過 Service 存取商業邏輯。

---

## 4. service / service.impl

負責商業邏輯與資料驗證。

| 介面 | 實作 | 說明 |
|---|---|---|
| MemberService | MemberServiceImpl | 會員登入、註冊、CRUD |
| ProductService | ProductServiceImpl | 商品 CRUD、上下架 |
| CategoryService | CategoryServiceImpl | 商品分類 |
| OrderService | OrderServiceImpl | 訂單建立、查詢、明細 |
| DashboardService | DashboardServiceImpl | 首頁資訊 |

原則：

- Controller 不直接操作 DAO。
- Service 負責檢查資料是否合法。
- Service 決定錯誤訊息與例外處理。

---

## 5. dao / dao.impl

負責資料庫存取。

| 介面 | 實作 | 說明 |
|---|---|---|
| MemberDao | MemberDaoImpl | member 資料表 |
| ProductDao | ProductDaoImpl | product 資料表 |
| CategoryDao | CategoryDaoImpl | category 資料表 |
| OrderDao | OrderDaoImpl | orders 資料表 |
| OrderDetailDao | OrderDetailDaoImpl | order_detail 資料表 |

原則：

- DAO 只處理 SQL 與資料轉換。
- 不處理 Swing UI。
- 不處理畫面提示文字。

---

## 6. model

Model 對應資料表與系統資料物件。

| Model | 對應內容 |
|---|---|
| Member | 會員 |
| Category | 商品分類 |
| Product | 商品 |
| Order | 訂單主檔 |
| OrderDetail | 訂單明細 |
| Dashboard | 首頁統計資料 |

---

## 7. util

共用工具類別。

| 檔案 | 說明 |
|---|---|
| DBConnection.java | MySQL 連線 |
| UIStyle.java | UI / CIS 共用樣式 |
| PdfUtil.java | 訂單列印 PDF 產生 |
| ImageUtil.java | 圖片工具 |
| LoginSession.java | 登入狀態 |
| OrderNoUtil.java | 訂單編號產生 |
| Validator.java | 共用驗證 |
| LogUtil.java | Log 工具 |

---

## 8. UIStyle.java 使用原則

所有畫面應優先使用 `UIStyle.java` 管理：

- 字型
- 顏色
- Button 樣式
- JTable 樣式
- Border
- Dialog 字型
- 欄寬設定

避免在各個 UI 類別中重複定義顏色與字型。

---

## 9. 套餐描述規則

本專案中 `product.description` 定義為：

```text
套餐描述
```

規則：

- 套餐：顯示與使用描述。
- 一般商品：不顯示描述欄位。
- 資料庫欄位保留，不修改 schema。

