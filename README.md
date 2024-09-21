
# 文大電器行購物網站

此專案為一個基於 **Spring Boot** 的後端系統與 **React** 前端的完整應用程式，包含購物車、用戶註冊、登入、產品管理等功能。

## 專案結構

- **後端 (Spring Boot)**
  - 目錄：`demo`
  - 使用了 **Maven** 進行管理，`pom.xml` 包含了所有相依性。
  - 項目入口：`src/main/java`
  - 測試文件：`src/test/java`
  - `target` 資料夾會包含編譯後的輸出。

- **前端 (React)**
  - 目錄：`Component`
  - 各個 `.js` 檔案為 React 元件，例如：
    - `Profile.js`: 用戶資料頁面
    - `Cart.js`: 購物車頁面
    - `Register.js`: 註冊頁面
    - `Login.js`: 登入頁面
    - `Checkout.js`: 結帳頁面
    - 以及更多功能性的元件，如 `Nav.js`、`Footer.js` 等。

## 如何執行

### 後端

1. 確保你已經安裝 **JDK 11** 或更新版本。
2. 使用 **Maven** 編譯和打包專案：

   ```bash
   ./mvnw clean package
   ```

3. 執行 Spring Boot 應用：

   ```bash
   java -jar target/your-application.jar
   ```

### 前端

1. 確保你已經安裝 **Node.js** 和 **npm**。
2. 進入前端專案資料夾 (`Component`)，並安裝相依性：

   ```bash
   npm install
   ```

3. 啟動開發伺服器：

   ```bash
   npm start
   ```

## 主要功能

- 用戶註冊與登入
- 購物車功能
- 產品管理（新增、刪除、修改產品）
- 結帳與支付
- 訂單管理

## 相依技術

- **後端**：Spring Boot、Maven、Java、Spring Security
- **前端**：React、JavaScript、Bootstrap
- **資料庫**：可配置 MySQL、JPA

## 版本管理

請參閱 `.gitignore` 檔案中的忽略規則，保持版本管理的清潔。
