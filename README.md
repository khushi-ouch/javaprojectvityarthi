# Hostel Mess Inventory & Billing System

## CSE2006 – Java Programming Project

Developed by: Khushi Parashar
Registration No.: 25BAI10363
Institution: VIT Bhopal

---

## 1. Project Overview

This is a Java application for running a hostel mess: tracking inventory, taking student food orders, billing them, and reporting on sales. Admins manage stock and pull reports; students browse the menu, order, and check their order history.

It's built around the core CSE2006 syllabus — OOP, JDBC, file handling, exception handling, and multithreading — rather than any one of those in isolation.

---

## 2. Objectives

- Keep hostel mess inventory accurate without manual bookkeeping.
- Let students see what's actually available before ordering.
- Validate stock before an order is accepted, not after.
- Generate CSV sales reports on demand.
- Catch low-stock items automatically instead of relying on someone noticing.

---

## 3. Features

### Admin Module

- Add, update, and price inventory items.
- View current stock.
- Generate sales reports and export them to CSV.

### Student Module

- Browse the mess menu with live prices and availability.
- Place an order; the total bill is calculated automatically.
- View past orders.
- Get a specific error message if an order can't go through — not a crash.

### Automated Stock Monitor

A background thread checks inventory levels on its own schedule, independent of whatever the admin or student is doing, and writes low-stock alerts to `logs/alerts.log`.

### Database Layer

SQLite via JDBC, using `PreparedStatement` and `ResultSet` for all queries, with the schema created automatically on first run.

---

## 4. Technologies Used

| Technology             | Purpose                          |
| ----------------------- | --------------------------------- |
| Java                    | Application development           |
| SQLite                  | Database management                |
| JDBC                    | Database connectivity              |
| Collections Framework   | Managing inventory and orders      |
| Multithreading          | Background stock monitoring        |
| CSV File Handling       | Importing and exporting data       |
| File I/O                | Logging and report generation      |
| OOP                     | Application structure and design   |

---

## 5. Project Structure

```text
HostelMessInventory/
│
├── src/
│   └── com/
│       └── hostelmess/
│           │
│           ├── Main.java
│           │
│           ├── model/
│           │   ├── Person.java
│           │   ├── Admin.java
│           │   ├── Student.java
│           │   ├── FoodItem.java
│           │   └── Order.java
│           │
│           ├── exception/
│           │   ├── InsufficientStockException.java
│           │   ├── InvalidQuantityException.java
│           │   └── ItemNotFoundException.java
│           │
│           ├── db/
│           │   ├── DatabaseConnection.java
│           │   └── DatabaseInitializer.java
│           │
│           ├── util/
│           │   ├── Logger.java
│           │   └── CSVUtil.java
│           │
│           ├── service/
│           │   ├── InventoryService.java
│           │   ├── BillingService.java
│           │   └── ReportService.java
│           │
│           └── monitor/
│               └── StockMonitor.java
│
├── data/
│   └── seed.csv
│
├── logs/
│   └── alerts.log
│
├── lib/
│   └── sqlite-jdbc.jar
│
├── README.md
├── statement.md
└── mess.db
```

---

## 6. Java Concepts Implemented

### 6.1 Object-Oriented Programming

Classes and objects, encapsulation, inheritance, abstraction, polymorphism:

```text
Person
 ├── Admin
 └── Student
```

### 6.2 Interfaces

`Discountable` handles discount logic for eligible items — a small, real use of an interface rather than one added just to check a syllabus box.

### 6.3 Custom Exceptions

- `InsufficientStockException`
- `InvalidQuantityException`
- `ItemNotFoundException`

### 6.4 Collections

`Map` backs inventory lookups; `List` backs orders and report data.

### 6.5 Multithreading

`StockMonitor` runs as a background daemon thread, polling stock levels on its own.

### 6.6 JDBC

`Connection`, `PreparedStatement`, `ResultSet` — no ORM.

### 6.7 File Handling

`BufferedReader` for reading CSV, `PrintWriter` for writing reports, and file-based logging for alerts.

---

## 7. Database Design

### Inventory

| Field | Description         |
| ----- | -------------------- |
| id    | Unique item ID        |
| name  | Food item name         |
| price | Price of item           |
| stock | Available quantity       |

### Students

| Field           | Description          |
| ---------------- | ---------------------- |
| id               | Student ID               |
| name             | Student name               |
| registration_no  | Registration number         |

### Orders

| Field      | Description            |
| ----------- | ------------------------ |
| id         | Unique order ID            |
| student_id | Student placing order        |
| total      | Total bill amount              |
| order_date | Date of order                    |

---

## 8. How to Run the Project

### Prerequisites

1. Java JDK 17 or later.
2. SQLite JDBC driver.
3. An IDE (IntelliJ, Eclipse, VS Code) or a terminal.
4. Git, if you're pulling from GitHub.

### Step 1: Clone the repository

```bash
git clone <your-github-repository-url>
cd HostelMessInventory
```

### Step 2: Add the SQLite JDBC driver

Drop the JAR into `lib/`:

```text
lib/sqlite-jdbc.jar
```

### Step 3: Compile

```bash
javac -cp "lib/sqlite-jdbc.jar" -d out $(find src -name "*.java")
```

On Windows, `find` isn't available by default — compile through your IDE, or list the source files another way.

### Step 4: Run

Linux/macOS:
```bash
java -cp "out:lib/sqlite-jdbc.jar" com.hostelmess.Main
```

Windows:
```bash
java -cp "out;lib/sqlite-jdbc.jar" com.hostelmess.Main
```

---

## 9. Sample Workflow

```text
Start Application
       |
       v
Initialize Database
       |
       v
Load Seed CSV Data
       |
       v
Display Main Menu
       |
       ├── Admin
       |     ├── Manage Inventory
       |     └── Generate Reports
       |
       └── Student
             ├── View Menu
             ├── Place Order
             └── View Order History
       |
       v
Background Stock Monitor
       |
       v
Low Stock Alert Logging
```

---

## 10. Sample Seed Data

```csv
id,name,price,stock
1,Vegetable Thali,60,50
2,Paneer Rice,80,30
3,Masala Dosa,40,25
4,Tea,15,100
5,Cold Coffee,50,20
```

---

## 11. Testing

| Test Case                     | Result |
| ------------------------------ | ------- |
| Database initialization         | Passed   |
| Inventory loading                 | Passed    |
| Student ordering                    | Passed     |
| Bill total calculation                | Passed      |
| Stock validation                        | Passed       |
| Insufficient stock rejection              | Passed        |
| CSV report generation                       | Passed         |
| Order history                                 | Passed          |
| Low-stock monitoring                            | Passed           |
| Alert log generation                              | Passed            |
| Multithreaded execution                             | Passed             |
| Order ID generation                                   | Passed              |

### Bugs found and fixed during testing

1. **Startup ordering** — the database schema wasn't ready before a service constructor tried to query it; fixed by initializing the schema first.
2. **Thread-safety** — concurrent access to the inventory cache from the monitor thread caused inconsistent reads; fixed with synchronized access and defensive snapshot copies.
3. **Order ID display** — the database-generated order ID wasn't being read back correctly; order confirmations now show the real ID.

---

## 12. Non-Functional Requirements

**Performance** — inventory lookups and order processing stay fast at the scale a single hostel mess actually needs.

**Reliability** — custom exceptions and stock validation stop invalid orders from being written to the database in the first place.

**Maintainability** — models, database code, utilities, services, and the monitor are in separate packages, so a change in one doesn't ripple through the others.

**Usability** — menu-driven, no documentation required to use it.

**Security** — all queries go through `PreparedStatement`, which rules out SQL injection through user input.

**Scalability** — the layered structure leaves room for things like payments or attendance tracking later without a rewrite.

---

## 13. Future Enhancements

- A GUI (JavaFX or Swing) instead of the console menu.
- Student login and authentication.
- Online payment integration.
- Daily/monthly sales analytics.
- Mess attendance tracking.
- Email or push alerts for low stock.
- Role-based access control.
- A centralized database instead of a local SQLite file.

---

## 14. Conclusion

This project covers most of the CSE2006 syllabus in one place — OOP, collections, JDBC, custom exceptions, file handling, and multithreading — applied to an actual problem rather than isolated exercises. What's here is a working inventory and billing system for a hostel mess, not a proof of concept: it handles real orders, rejects invalid ones, and tracks stock on its own.

---

## 15. Author

**Name:** Utkarsh Sharma  
**Registration No.:** 25BAI11303  
**Course:** CSE2006 – Java Programming  
**Institution:** VIT

---

## License

Built for academic and educational purposes.
