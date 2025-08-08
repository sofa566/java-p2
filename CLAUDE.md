# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案概述

這是一個 B2B 管理系統，包含分離的後端和前端應用程式：

- **後端**: Spring Boot 3.2.0 應用程式，使用 Java 17，提供使用者管理、店舖管理、計費帳戶、發票和付款的 REST API
- **前端**: React + TypeScript + Vite 應用程式，提供使用者介面
- **資料庫**: MySQL，具備多租戶 B2B 營運的完整架構

## 開發命令

### 後端 (Maven + Spring Boot)
- **建置**: `cd backend && mvn clean compile`
- **執行**: `cd backend && mvn spring-boot:run`
- **測試**: `cd backend && mvn test`
- **打包**: `cd backend && mvn clean package`

### 前端 (Node.js + Vite)
- **安裝依賴**: `cd frontend && npm install`
- **開發伺服器**: `cd frontend && npm run dev`
- **建置**: `cd frontend && npm run build`
- **程式碼檢查**: `cd frontend && npm run lint`
- **預覽**: `cd frontend && npm run preview`

### 資料庫設定
- MySQL 資料庫 `b2b_management` 會在首次執行時自動建立
- 架構位於 `backend/src/main/resources/schema.sql`
- 應用程式使用 Hibernate DDL 自動更新模式

## 架構與核心元件

### 後端架構 (Spring Boot)
- **控制器層**: REST 端點位於 `com.b2b.controller.*`
  - AuthController: 身份驗證和 JWT 令牌管理
  - BillingController: 計費帳戶操作
  - StoreController: 店舖管理
  - UserController: 使用者管理操作

- **服務層**: 業務邏輯位於 `com.b2b.service.*`
  - 計費、發票、付款的事務操作
  - 使用者驗證和授權邏輯
  - 店舖-使用者關係管理

- **儲存庫層**: JPA 儲存庫位於 `com.b2b.repository.*`
  - Spring Data JPA 進行資料庫操作
  - 複雜業務操作的自訂查詢方法

- **安全性**: 基於 JWT 的身份驗證和 Spring Security
  - JwtAuthenticationFilter 用於請求過濾
  - 基於角色的存取控制 (ADMIN, STORE_MANAGER, USER)

### 實體關係
- **User** ↔ **Store**: 透過 StoreUser 的多對多關係 (具有角色)
- **Store** → **BillingAccount**: 一對多
- **BillingAccount** → **Invoice**: 一對多
- **Invoice** → **InvoiceItem**: 一對多
- **Invoice** → **Payment**: 一對多

### 核心業務邏輯
- **多租戶**: 店舖作為租戶，與相關使用者
- **財務操作**: 餘額追蹤、信用額度、發票生成
- **基於角色的存取**: 店舖管理員可以管理他們的店舖，系統管理員管理系統

### 前端架構 (React + TypeScript)
- **Vite**: 建置工具和開發伺服器
- **TypeScript**: 嚴格配置的類型安全
- **ESLint**: 程式碼品質和格式化

## 配置說明

### 資料庫連線
- 預設: MySQL 在 localhost:3306
- 資料庫: `b2b_management` (自動建立)
- 認證資訊在 `application.yml` 中配置

### 安全配置
- JWT 令牌過期時間: 24 小時
- CORS 配置用於跨源請求
- 基於角色的端點保護

### 應用程式連接埠
- 後端: 8080 (Spring Boot)
- 前端開發伺服器: Vite 預設 (通常是 5173)

## 開發工作流程

### 新增功能
1. 如需要，在 `com.b2b.entity` 中建立實體
2. 在 `com.b2b.repository` 中新增儲存庫介面
3. 在 `com.b2b.service` 中實作服務邏輯
4. 在 `com.b2b.dto` 中為請求/回應建立 DTO
5. 在 `com.b2b.controller` 中新增控制器端點
6. 如需要，更新資料庫架構

### 測試策略
- 服務層業務邏輯的單元測試
- 儲存庫操作的整合測試
- 身份驗證/授權的安全測試
- 目前不存在測試 - 應該解決這個問題

### 資料庫變更
- 開發使用 Hibernate DDL 自動更新
- 生產部署應使用架構遷移腳本
- 所有實體使用標準 JPA 註解

## 重要考慮事項

### 安全說明
- JWT 金鑰在生產環境中應該外部化
- 資料庫認證應使用環境變數
- 角色權限在服務層強制執行

### 效能考慮事項
- 實體關係配置為延遲載入
- 在 schema.sql 中定義資料庫索引
- 列表操作實作分頁

### 財務資料處理
- 所有貨幣值使用 DECIMAL(15,2) 精度
- 餘額操作應該是原子的和線程安全的
- 發票計算包含稅務處理