USE realestatehub;

-- 1. SETUP & CLEANUP
-- Disable safety checks to allow deleting tables with relationships
SET FOREIGN_KEY_CHECKS = 0;

-- Drop old tables if they exist (Start Fresh)
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS buyers;
DROP TABLE IF EXISTS sellers;

-- ============================================
-- 2. CREATE TABLE STRUCTURE
-- ============================================

CREATE TABLE sellers (
                         user_id CHAR(36) PRIMARY KEY,
                         username VARCHAR(50),
                         email VARCHAR(100),
                         password VARCHAR(255),
                         first_name VARCHAR(50),
                         last_name VARCHAR(50),
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE buyers (
                        user_id CHAR(36) PRIMARY KEY,
                        username VARCHAR(50),
                        email VARCHAR(100),
                        password VARCHAR(255),
                        first_name VARCHAR(50),
                        last_name VARCHAR(50),
                        budget DECIMAL(15, 2),
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE properties (
                            property_id CHAR(36) PRIMARY KEY,
                            owner_id CHAR(36),
                            title VARCHAR(255),
                            description TEXT,
                            location VARCHAR(255),
                            price DECIMAL(15, 2),
                            size DECIMAL(10, 2),
                            property_type VARCHAR(50),
                            status VARCHAR(50),
                            bedrooms INT,
                            bathrooms INT,
                            has_garage BOOLEAN,
                            has_pool BOOLEAN,
                            has_garden BOOLEAN,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (owner_id) REFERENCES sellers(user_id) ON DELETE CASCADE
);

-- ============================================
-- 3. GENERATE DATA
-- ============================================

-- Generate Sellers (Original Swiss/French/German Mix)
DROP PROCEDURE IF EXISTS generate_sellers;
DELIMITER //
CREATE PROCEDURE generate_sellers()
BEGIN
    DECLARE i INT DEFAULT 0;
    -- Original List kept for Sellers
    DECLARE first_names VARCHAR(1000) DEFAULT 'Marc,Sophie,Laurent,Anna,Pierre,Marie,Thomas,Julie,Nicolas,Emma,Lucas,Lea,David,Sarah,Michael,Laura,Daniel,Camille,Philippe,Celine,Andre,Nathalie,Francois,Isabelle,Jean,Catherine,Patrick,Valerie,Olivier,Sandrine,Yves,Martine,Alain,Christine,Bruno,Sylvie,Christian,Monique,Eric,Francoise,Claude,Jacqueline,Michel,Danielle,Robert,Nicole,Antoine,Caroline,Sebastien,Aurelie';
    DECLARE last_names VARCHAR(1000) DEFAULT 'Mueller,Dubois,Favre,Schneider,Weber,Bonvin,Rochat,Martin,Bernard,Moser,Keller,Brunner,Schmid,Meyer,Huber,Gerber,Steiner,Fischer,Baumann,Zimmermann,Graf,Frei,Roth,Beck,Wyss,Lehmann,Kunz,Wagner,Hofmann,Bauer,Koch,Sutter,Vogel,Rey,Blanc,Chevalier,Girard,Fournier,Lambert,Dumont,Mercier,Richard,Simon,Laurent,Leroy,Moreau,Garcia,Roux,Vincent,Perrin';

    WHILE i < 1000 DO
        INSERT INTO sellers (user_id, username, email, password, first_name, last_name, created_at, updated_at)
        VALUES (
            UUID(),
            CONCAT(LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(first_names, ',', 1 + (i MOD 50)), ',', -1)), '.', LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(last_names, ',', 1 + ((i DIV 50 + i) MOD 50)), ',', -1)), i),
            CONCAT(LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(first_names, ',', 1 + (i MOD 50)), ',', -1)), '.', LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(last_names, ',', 1 + ((i DIV 50 + i) MOD 50)), ',', -1)), i, '@',
                   ELT(1 + (i MOD 5), 'gmail.com', 'bluewin.ch', 'sunrise.ch', 'outlook.com', 'immobilier.ch')),
            'pass123',
            SUBSTRING_INDEX(SUBSTRING_INDEX(first_names, ',', 1 + (i MOD 50)), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(last_names, ',', 1 + ((i DIV 50 + i) MOD 50)), ',', -1),
            NOW(), NOW()
        );
        SET i = i + 1;
END WHILE;
END //
DELIMITER ;

CALL generate_sellers();

-- Generate Buyers (New International/English/Italian Mix)
DROP PROCEDURE IF EXISTS generate_buyers;
DELIMITER //
CREATE PROCEDURE generate_buyers()
BEGIN
    DECLARE i INT DEFAULT 0;
    -- NEW Distinct List for Buyers
    DECLARE first_names VARCHAR(1000) DEFAULT 'James,Elena,Oliver,Maria,Noah,Sophia,Liam,Isabella,William,Mia,Benjamin,Charlotte,Lucas,Amelia,Henry,Harper,Alexander,Evelyn,Sebastian,Abigail,Mateo,Emily,Jackson,Elizabeth,Aiden,Mila,Samuel,Ella,Matthew,Avery,Joseph,Sofia,Levi,Camila,Daniel,Aria,David,Scarlett,John,Victoria,Carter,Madison,Luke,Luna,Gabriel,Grace,Isaac,Chloe,Jayden,Penelope';
    DECLARE last_names VARCHAR(1000) DEFAULT 'Smith,Johnson,Rossi,Brown,Jones,Bianchi,Miller,Davis,Rodriguez,Martinez,Hernandez,Lopez,Gonzalez,Wilson,Anderson,Thomas,Taylor,Moore,Jackson,Martin,Lee,Perez,Thompson,White,Harris,Sanchez,Clark,Ramirez,Lewis,Robinson,Walker,Young,Allen,King,Wright,Scott,Torres,Nguyen,Hill,Flores,Green,Adams,Nelson,Baker,Hall,Rivera,Campbell,Mitchell,Carter,Roberts';
    DECLARE round_budgets VARCHAR(500) DEFAULT '350000,450000,500000,550000,600000,650000,700000,750000,800000,850000,900000,950000,1000000,1100000,1200000,1300000,1400000,1500000,1750000,2000000,2250000,2500000,3000000,3500000,4000000';

    WHILE i < 1000 DO
        INSERT INTO buyers (user_id, username, email, password, first_name, last_name, budget, created_at, updated_at)
        VALUES (
            UUID(),
            CONCAT(LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(first_names, ',', 1 + (i MOD 50)), ',', -1)), '.', LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(last_names, ',', 1 + ((i DIV 50 + i) MOD 50)), ',', -1)), i),
            CONCAT(LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(first_names, ',', 1 + (i MOD 50)), ',', -1)), '.', LOWER(SUBSTRING_INDEX(SUBSTRING_INDEX(last_names, ',', 1 + ((i DIV 50 + i) MOD 50)), ',', -1)), i, '@',
                   ELT(1 + (i MOD 6), 'gmail.com', 'bluewin.ch', 'outlook.com', 'protonmail.ch', 'icloud.com', 'hotmail.com')),
            'pass123',
            SUBSTRING_INDEX(SUBSTRING_INDEX(first_names, ',', 1 + (i MOD 50)), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(last_names, ',', 1 + ((i DIV 50 + i) MOD 50)), ',', -1),
            CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(round_budgets, ',', 1 + (i MOD 25)), ',', -1) AS UNSIGNED),
            NOW(), NOW()
        );
        SET i = i + 1;
END WHILE;
END //
DELIMITER ;

CALL generate_buyers();

-- Generate Properties
DROP PROCEDURE IF EXISTS generate_properties;
DELIMITER //
CREATE PROCEDURE generate_properties()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE seller_id VARCHAR(36);
    DECLARE streets VARCHAR(2000) DEFAULT 'Rue du Rhone,Bahnhofstrasse,Avenue de la Gare,Rue de Bourg,Kramgasse,Freiestrasse,Rue du Mont-Blanc,Limmatquai,Place Saint-Francois,Marktgasse,Rue de Lausanne,Bleicherweg,Avenue de Champel,Rennweg,Rue des Alpes,Augustinergasse,Quai du Mont-Blanc,Talstrasse,Rue de Rive,Munstergasse,Avenue Pictet,Fortunagasse,Rue de Carouge,Uraniastrasse,Place Bel-Air,Bahnhofplatz,Rue du Stand,Pelikanstrasse,Avenue de Frontenex,Stockerstrasse,Rue de Contamines,Seefeldstrasse,Boulevard des Philosophes,Ramistrasse,Rue Voltaire,Hottingerstrasse,Avenue de Miremont,Dufourstrasse,Rue de Athenee,Sihlstrasse,Route de Florissant,Lowenstrasse,Rue du Conseil,Weinbergstrasse,Avenue Louis-Casai,Birmensdorferstrasse,Rue de Chantepoulet,Badenerstrasse,Cours de Rive,Hardstrasse';
    DECLARE cities VARCHAR(500) DEFAULT '1204 Geneve,8001 Zurich,1003 Lausanne,3011 Bern,4001 Basel,6900 Lugano,1700 Fribourg,2000 Neuchatel,1950 Sion,6000 Luzern,8400 Winterthur,9000 St Gallen,2501 Biel,1400 Yverdon,6600 Locarno,3900 Brig,1820 Montreux,8200 Schaffhausen,7000 Chur,4600 Olten';
    DECLARE descriptions VARCHAR(2000) DEFAULT 'Magnifique bien immobilier avec vue panoramique,Superbe appartement lumineux au coeur de la ville,Charmante maison familiale proche commodites,Luxueuse propriete avec finitions haut de gamme,Bel espace de vie moderne et fonctionnel,Propriete exceptionnelle dans quartier prise,Appartement renove avec gout et materiaux nobles,Maison spacieuse avec grand jardin arbore,Bien rare sur le marche a ne pas manquer,Emplacement ideal proche transports publics';
    DECLARE round_prices VARCHAR(1000) DEFAULT '395000,450000,495000,525000,575000,625000,695000,750000,795000,850000,895000,950000,995000,1095000,1195000,1295000,1395000,1495000,1595000,1795000,1950000,2195000,2450000,2750000,2950000,3250000,3750000,4250000,4950000,5950000,6950000,7950000';

    WHILE i < 1000 DO
SELECT user_id INTO seller_id FROM sellers ORDER BY RAND() LIMIT 1;

INSERT INTO properties (property_id, owner_id, title, description, location, price, size, property_type, status, bedrooms, bathrooms, has_garage, has_pool, has_garden, created_at, updated_at)
VALUES (
           UUID(),
           seller_id,
           CONCAT(ELT(1 + (i MOD 5), 'HOUSE', 'APARTMENT', 'CONDO', 'VILLA', 'TOWNHOUSE'), ' - ', SUBSTRING_INDEX(SUBSTRING_INDEX(cities, ',', 1 + (i MOD 20)), ',', -1)),
           SUBSTRING_INDEX(SUBSTRING_INDEX(descriptions, ',', 1 + (i MOD 10)), ',', -1),
           CONCAT(SUBSTRING_INDEX(SUBSTRING_INDEX(cities, ',', 1 + (i MOD 20)), ',', -1), ', ', SUBSTRING_INDEX(SUBSTRING_INDEX(streets, ',', 1 + (i MOD 50)), ',', -1), ' ', 1 + (i MOD 150)),
           CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(round_prices, ',', 1 + (i MOD 32)), ',', -1) AS UNSIGNED),
           50 + FLOOR(RAND() * 450),
           ELT(1 + (i MOD 5), 'HOUSE', 'APARTMENT', 'CONDO', 'VILLA', 'TOWNHOUSE'),
           'FOR_SALE',
           1 + (i MOD 6),
           1 + (i MOD 4),
           (i MOD 2) = 0,
           (i MOD 5) = 0,
           (i MOD 3) = 0,
           NOW(), NOW()
       );
SET i = i + 1;
END WHILE;
END //

DELIMITER ;

CALL generate_properties();

-- Cleanup
DROP PROCEDURE IF EXISTS generate_sellers;
DROP PROCEDURE IF EXISTS generate_buyers;
DROP PROCEDURE IF EXISTS generate_properties;

SET FOREIGN_KEY_CHECKS = 1;

-- Verify
SELECT 'Sellers' AS table_name, COUNT(*) AS count FROM sellers
UNION ALL
SELECT 'Buyers', COUNT(*) FROM buyers
UNION ALL
SELECT 'Properties', COUNT(*) FROM properties;
