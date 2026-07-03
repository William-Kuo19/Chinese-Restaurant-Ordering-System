# 🍽 Chinese Restaurant Ordering System

![Java](https://img.shields.io/badge/Java-11-orange)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue)
![Maven](https://img.shields.io/badge/Build-Maven-red)
![MySQL](https://img.shields.io/badge/Database-MySQL%208-4479A1)
![Architecture](https://img.shields.io/badge/Architecture-MVC%20%2B%20DAO-success)
![License](https://img.shields.io/badge/License-MIT-green)

> Desktop POS System built with Java Swing.

使用 **Java Swing** 開發的中式餐廳點餐管理系統，採用 **MVC + DAO Pattern** 架構，提供會員管理、商品管理、點餐、訂單查詢與列印等完整功能。

---

## ✨ Features

- 👤 Member Management (CRUD)
- 🍜 Product Management (CRUD)
- 🛒 Shopping Cart
- 📄 Order Management
- 🖨 Print / Reprint Order
- 🇹🇼 Traditional Chinese User Interface
- 🏗 MVC + DAO Architecture
- ☕ Java Swing Desktop Application

---

## 🛠 Technology Stack

| Item | Technology |
|------|------------|
| Language | Java 11 |
| GUI | Java Swing |
| IDE | Eclipse WindowBuilder |
| Build Tool | Maven |
| Database | MySQL 8 |
| Architecture | MVC + DAO Pattern |

---

## 📁 Project Structure

```
src
├── controller
├── dao
│   └── impl
├── service
│   └── impl
├── model
├── util
└── exception
```

---

## 🚀 Getting Started

### 1. Clone Repository

```bash
git clone https://github.com/your-account/Chinese-Restaurant-Ordering-System.git
```

### 2. Import as Maven Project

Import the project into Eclipse as an existing Maven project.

### 3. Create Database

Execute the SQL script:

```
sql/restaurant.sql
```

### 4. Configure Database Connection

Modify the database configuration in your DB connection utility.

### 5. Run Application

Run

```
LoginUI.java
```

---

## 📂 Documentation

- DATABASE.md
- PROJECT_STRUCTURE.md

---

## 📄 License

MIT License
