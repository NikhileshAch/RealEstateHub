# RealEstateHub - Phase 2 Demo Guide

## Quick Start

### Access the Application
- **URL:** http://localhost:8080/WebApplication-1.0-SNAPSHOT/index.xhtml
- **Admin Console:** http://localhost:4848
- **REST API:** http://localhost:8080/WebService_RealsEstateHub-1.0-SNAPSHOT/api

---

## Demo Workflow 1: Buyer Registration & Property Search

### Step 1: Register as New Buyer
1. Go to index.xhtml
2. Click "Continue as Buyer" or navigate to `/register.xhtml`
3. Fill in:
   - First Name: `Demo`
   - Last Name: `Buyer`
   - Email: `demobuy@demo.com`
   - Username: `demobuy`
   - Password: `demo123`
   - Confirm Password: `demo123`
4. Select "BUYER" role
5. The budget field appears via AJAX (conditional rendering)
6. Enter Budget: `400000`
7. Click "Register"
8. ✅ Auto-login and redirect to `/buyer/properties.xhtml`

### Step 2: Browse & Filter Properties
1. View the "Browse Properties" page with demo properties
2. **Test Filters:**
   - Search Location: Type "Street" in location search
   - Property Type: Select from dropdown
   - Price Range: Enter min/max prices
   - Min Bedrooms: Filter by bedroom count
   - Status: Select "FOR_SALE" only
   - Sort By: Choose "Price" or "Bedrooms"
3. ✅ Table updates dynamically with AJAX
4. Note: Status badges show color-coded indicators

### Step 3: Make an Offer
1. Click "Make Offer" on any FOR_SALE property
2. System pre-fills the property ID
3. Enter offer details:
   - Offer Amount: `250000`
   - Message to Seller: `I'm very interested in this property!`
4. Click "Submit Offer"
5. ✅ Redirected to My Offers page with success message

### Step 4: Track Your Offers
1. At `/buyer/my-offers.xhtml`
2. View all submitted offers with:
   - Property ID
   - Offer Amount (formatted currency)
   - Status badge (PENDING/ACCEPTED/REJECTED)
   - Offer message
   - Creation date
3. Filter by Status: PENDING, ACCEPTED, or REJECTED
4. ✅ See offer progression

---

## Demo Workflow 2: Seller Listing & Offer Management

### Step 1: Register as New Seller
1. Go to `/register.xhtml`
2. Fill in seller information:
   - First Name: `Demo`
   - Last Name: `Seller`
   - Email: `demosell@demo.com`
   - Username: `demosell`
   - Password: `demo123`
   - Confirm Password: `demo123`
3. Select "SELLER" role
4. ✅ Budget field disappears (AJAX conditional rendering)
5. Click "Register"
6. ✅ Auto-login and redirect to `/seller/my-properties.xhtml`

### Step 2: Add a New Property
1. Click "Add Property" button in navbar
2. Goes to `/seller/add-property.xhtml`
3. Fill in all fields:
   - Address: `123 Oak Street, Lausanne`
   - Price: `450000`
   - Bedrooms: `4`
   - Bathrooms: `2`
   - Square Footage: `2500`
   - Property Type: Select "HOUSE"
   - Description: `Beautiful family home with garden`
   - Features (h:selectBooleanCheckbox):
     - ☑ Has Garage
     - ☑ Has Pool
     - ☐ Has Garden
4. Click "Add Property"
5. ✅ Property created and added to list
6. ✅ Success message displayed

### Step 3: View Your Properties
1. At `/seller/my-properties.xhtml`
2. Table shows:
   - Address
   - Price (formatted currency)
   - Type
   - Beds/Baths ratio
   - Status badge (FOR_SALE - green)
3. Test Filters:
   - Status dropdown: FOR_SALE, PENDING, SOLD
   - Sort by: Price, Bedrooms
   - Clear Filters button
4. ✅ Dynamic AJAX updates

### Step 4: Edit Property
1. Click "Edit" button on your property
2. Form pre-fills with current values
3. Update any field (e.g., Price: `475000`)
4. Click "Update Property"
5. ✅ Changes saved and table refreshes

### Step 5: Delete Property (Optional)
1. Click "Delete" button with confirmation
2. JavaScript confirm() dialog appears
3. Click "OK" to confirm deletion
4. ✅ Property removed from list

### Step 6: Manage Offers
1. Buyers from Step 1 make offers on your property
2. Click "Manage Offers" in navbar
3. At `/seller/manage-offers.xhtml`
4. View all offers:
   - Property ID
   - Buyer info
   - Offer Amount
   - Status (PENDING initially)
5. Actions:
   - Click "Accept" to accept offer
   - Click "Reject" to decline
6. ✅ Status updated to ACCEPTED or REJECTED
7. Status badge color changes immediately

---

## Testing Key Features

### Authentication & Session Management
- ✅ Login with demo credentials (alice@demo.com / pass123)
- ✅ Logout clears session
- ✅ Protected pages redirect to login when not authenticated
- ✅ Role-based redirect (Buyer → /buyer/properties, Seller → /seller/my-properties)

### Form Validation
- ✅ Empty fields show "required" error
- ✅ Email format validation
- ✅ Password matching validation
- ✅ Price > 0 validation
- ✅ Bedrooms/bathrooms > 0 validation
- ✅ Error messages styled in red with icons

### UI Components
| Component | Location | Usage |
|-----------|----------|-------|
| h:selectOneMenu | All filter dropdowns | Role, Type, Status, Sort By |
| h:inputText | Forms, search boxes | Email, Address, Price, Location |
| h:commandButton | All pages | Login, Register, Submit, Add, Update, Delete |
| h:inputSecret | Login/Register | Password input |
| h:messages | All forms | Error/success feedback |
| h:selectBooleanCheckbox | Add Property | Garage, Pool, Garden features |
| h:inputTextarea | Offers, Property | Descriptions, Messages |
| h:commandLink | Tables | Edit, Delete, Make Offer |
| h:dataTable | All listings | Properties, Offers, Buyers, Sellers |
| f:ajax | Filters, Register | Dynamic updates without page reload |

### Styling & Responsive Design
- ✅ Gradient purple-to-violet branding
- ✅ Status badges color-coded:
  - 🟢 Green: FOR_SALE, ACCEPTED
  - 🟠 Orange: PENDING
  - 🔴 Red: SOLD, REJECTED
- ✅ Hover effects on buttons and tables
- ✅ Mobile-responsive grid layout
- ✅ Professional form styling with consistent spacing

### REST API Integration
- ✅ All data fetches from REST API
- ✅ Real-time data updates
- ✅ Proper HTTP status handling
- ✅ Error messages from API failures
- ✅ Currency conversion in displays

---

## Demo Credentials (Pre-loaded)

### Buyers
| Email | Username | Password | Budget |
|-------|----------|----------|--------|
| alice@demo.com | alice | pass123 | $350,000 |
| jonathan.grossrieder@unil.ch | Jon | pass456 | $550,000 |

### Sellers
| Email | Username | Password |
|-------|----------|----------|
| demo.seller@demo.com | demoSeller | pass789 |

---

## Grading Checklist

### Core Features (4.0 points)
- ✅ CRUD Operations - Full property creation, reading, updating, deletion
- ✅ Authentication - Login/register system with validation
- ✅ Authorization - Role-based access (Buyer vs Seller)
- ✅ Buyer Features - Browse, search, filter, make offers
- ✅ Seller Features - List, edit, delete, manage offers
- ✅ Form Validation - Comprehensive input validation
- ✅ Error Handling - FacesMessage feedback

### Advanced UI (1.5 points)
- ✅ h:selectOneMenu - Multiple dropdown filters
- ✅ h:inputText - Text inputs with validation
- ✅ h:commandButton - Multiple button styles
- ✅ h:inputSecret - Password fields
- ✅ h:messages - Error display
- ✅ h:selectBooleanCheckbox - Property features (EXTRA)
- ✅ h:inputTextarea - Text areas (EXTRA)
- ✅ h:commandLink - Navigation links (EXTRA)
- ✅ h:dataTable - Sortable tables (EXTRA)
- ✅ f:ajax - Dynamic filtering (EXTRA)

### Code Quality (0.5 points)
- ✅ Clean architecture with CDI beans
- ✅ Professional styling with CSS
- ✅ Responsive design
- ✅ Proper error handling
- ✅ Security (authentication filter, session management)

---

## Troubleshooting

### Port Already in Use
```bash
docker-compose down
docker-compose up -d
```

### Applications Not Deploying
```bash
# Check logs
docker logs payara-RealEstateHub

# Restart container
docker-compose restart
```

### Lost Changes After Restart
- All data is stored in ApplicationState (in-memory)
- Persists during session
- Clears on server restart (expected behavior)

---

## Build Commands (If Needed)

```bash
# Build WebApplication
cd "/Users/nikhil/Downloads/Software Architectures/RealEstateHub/WebApplication"
/Applications/IntelliJ\ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn clean package

# Build WebService
cd "/Users/nikhil/Downloads/Software Architectures/RealEstateHub/WebService_RealsEstateHub"
/Applications/IntelliJ\ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn clean package

# Deploy
cd "/Users/nikhil/Downloads/Software Architectures/RealEstateHub"
docker-compose restart
```

---

## Contact & Questions

For any issues or questions about the implementation, refer to:
- **PHASE2_IMPLEMENTATION.md** - Complete technical documentation
- **IntelliJ IDE** - Open project for code inspection
- **Browser Console** - Check for JavaScript errors

---

*RealEstateHub Phase 2 - Ready for Demonstration!*
