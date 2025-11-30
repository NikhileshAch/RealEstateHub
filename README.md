# RealEstateHub

A web-based real estate platform that connects property sellers with potential buyers. Sellers can list properties for sale, and buyers can browse listings and make offers.

## What Does This Application Do?

**For Sellers:**
- Register and log in to your account
- List properties for sale with details (address, price, size, bedrooms, etc.)
- View and manage your property listings
- Receive and respond to offers from buyers (accept/reject)

**For Buyers:**
- Register and log in to your account
- Browse available properties with filters (location, price range, property type)
- Make offers on properties you're interested in
- Track the status of your offers

---

## Project Structure

The project is organized into **three main components**:

```
RealEstateHub/
├── src/                    → Core domain models (shared code)
├── WebService_RealsEstateHub/  → REST API (backend)
├── WebApplication/         → Web interface (frontend)
└── docker-compose.yml      → Container configuration
```

### 1. Domain Models (`src/`)

These are the core **data structures** that represent the business concepts:

| Model | Purpose |
|-------|---------|
| `User.java` | Base class for all users (email, password, name) |
| `Buyer.java` | A user who can browse properties and make offers |
| `Seller.java` | A user who can list and manage properties |
| `Property.java` | A real estate listing (address, price, type, status) |
| `Offer.java` | A buyer's offer on a property (amount, status, message) |

### 2. REST API (`WebService_RealsEstateHub/`)

The **backend service** that handles all data operations. It provides endpoints that the web application calls to:

- Create, read, update, and delete users
- Manage property listings
- Process offers

**Key Files:**
- `BuyerResource.java` - API for buyer operations (`/api/buyers`)
- `SellerResource.java` - API for seller operations (`/api/sellers`)
- `PropertyResource.java` - API for property operations (`/api/properties`)
- `OfferResource.java` - API for offer operations (`/api/offers`)
- `ApplicationState.java` - In-memory data storage

### 3. Web Application (`WebApplication/`)

The **frontend** that users interact with in their browser. Built with JavaServer Faces (JSF) and PrimeFaces for a modern UI.

**Key Folders:**
- `webapp/` - Web pages (`.xhtml` files)
  - `login.xhtml`, `register.xhtml` - Authentication pages
  - `buyer/` - Pages for buyers (browse properties, my offers)
  - `seller/` - Pages for sellers (my properties, add property, manage offers)
- `beans/` - Java classes that connect pages to the API
  - `SessionBean.java` - Manages user login state
  - `PropertyBean.java` - Handles property operations
  - `OfferBean.java` - Handles offer operations

---

## Technologies Used

| Technology | What It Does | Why We Use It |
|------------|--------------|---------------|
| **Java 17** | Programming language | Industry standard, robust, and well-supported |
| **Jakarta EE 10** | Enterprise framework | Provides tools for building web applications |
| **JSF (JavaServer Faces)** | Web framework | Builds dynamic web pages with reusable components |
| **PrimeFaces** | UI component library | Pre-built modern UI elements (tables, buttons, forms) |
| **JAX-RS** | REST API framework | Makes it easy to create web services |
| **Payara Server** | Application server | Runs our Java web applications |
| **Docker** | Containerization | Packages the app so it runs the same everywhere |
| **Maven** | Build tool | Manages dependencies and builds the project |

---

## How It Works (Architecture)

```
┌─────────────────┐     HTTP      ┌─────────────────┐     HTTP      ┌─────────────────┐
│                 │   Requests    │                 │   Requests    │                 │
│    Browser      │ ───────────▶  │  WebApplication │ ───────────▶  │   WebService    │
│    (User)       │               │    (Frontend)   │               │   (REST API)    │
│                 │ ◀───────────  │                 │ ◀───────────  │                 │
│                 │   HTML Pages  │                 │   JSON Data   │                 │
└─────────────────┘               └─────────────────┘               └─────────────────┘
```

1. **User** opens the website in their browser
2. **WebApplication** serves the web pages and handles user interactions
3. **WebService** stores and retrieves data when requested
4. Data flows back through the chain to display to the user

---

## Running the Application

### Prerequisites
- Docker Desktop installed and running
- Java 17 (for development)
- Maven (for building)

### Quick Start

1. **Build the projects:**
   ```bash
   cd WebApplication && mvn clean package -DskipTests
   cd ../WebService_RealsEstateHub && mvn clean package -DskipTests
   ```

2. **Start the application:**
   ```bash
   docker-compose up -d
   ```

3. **Open in browser:**
   ```
   http://localhost:8080/WebApplication-1.0-SNAPSHOT/
   ```

4. **Stop the application:**
   ```bash
   docker-compose down
   ```

---

## Demo Accounts

For testing, you can register new accounts or use these if they exist:

| Role | Email | Password |
|------|-------|----------|
| Buyer | alice@demo.com | pass123 |
| Seller | seller@demo.com | pass789 |

---

## Key Features

- **Role-Based Access**: Buyers and sellers have different dashboards and capabilities
- **Property Filtering**: Search by location, price range, property type, and more
- **Offer Management**: Sellers can accept or reject offers with full visibility
- **Modern UI**: Clean, responsive design with PrimeFaces components
- **Real-Time Updates**: Changes reflect immediately across the application

---

## Project Contributors

- Nikhil
- Jonathan Grossrieder

---

## Notes

- Data is stored in memory and resets when the server restarts
- The application runs inside a Docker container for consistency
- Built as a university project for Software Architectures course
