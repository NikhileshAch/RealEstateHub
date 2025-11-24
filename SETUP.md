# RealEstateHub - Setup Instructions

## Environment Variables

To enable real email notifications, you need to set up the Brevo API key:

### Option 1: Environment Variable (Recommended for Production)

1. Get your free Brevo API key from: https://app.brevo.com/settings/keys/api
2. Set the environment variable before starting the application:

```bash
export BREVO_API_KEY="your-actual-api-key-here"
```

3. Then build and deploy:

```bash
mvn clean package
docker-compose up -d
```

### Option 2: Docker Environment (for docker-compose)

Add to your `docker-compose.yml`:

```yaml
services:
  payara:
    environment:
      - BREVO_API_KEY=your-actual-api-key-here
```

### Option 3: Payara Configuration

Set as JVM option in Payara:
```
-DBREVO_API_KEY=your-actual-api-key-here
```

## Building and Running

```bash
# Build all projects
mvn clean package

# Start with Docker
docker-compose up -d

# Check logs
docker logs payara-RealEstateHub -f
```

## Default Users

**Buyer:**
- Email: alice@demo.com
- Password: pass123

**Seller:**
- Email: demo.seller@demo.com
- Password: pass789

## Email Notifications

- **Without API key:** Email notifications are simulated (logged to console)
- **With API key:** Real emails are sent via Brevo API (300 free emails/day)

Check console logs for email notifications:
```bash
docker logs payara-RealEstateHub | grep -A 15 "EMAIL NOTIFICATION"
```
