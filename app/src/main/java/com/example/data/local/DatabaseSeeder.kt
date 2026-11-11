package com.example.data.local

import com.example.data.model.*

object DatabaseSeeder {

    suspend fun checkAndSeedIfEmpty(db: AppDatabase) {
        if (db.categoryDao().getCategoryCount() == 0) {
            seedDatabase(db)
        }
    }

    suspend fun seedDatabase(db: AppDatabase) {
        // 1. Initial Users
        val adminUser = UserEntity(
            id = "user_admin_1",
            email = "admin@bichkand.pk",
            passwordHash = "admin123",
            name = "Marketplace Admin",
            phone = "+92 300 0000000",
            role = "ADMIN"
        )
        val sellerUser1 = UserEntity(
            id = "user_seller_1",
            email = "seller@techhub.pk",
            passwordHash = "seller123",
            name = "Zain Ul Abideen",
            phone = "+92 321 5551234",
            role = "SELLER"
        )
        val sellerUser2 = UserEntity(
            id = "user_seller_2",
            email = "fatima@fashionhub.pk",
            passwordHash = "seller123",
            name = "Fatima Noor",
            phone = "+92 333 7778899",
            role = "SELLER"
        )
        val buyerUser = UserEntity(
            id = "user_buyer_1",
            email = "buyer@bichkand.pk",
            passwordHash = "buyer123",
            name = "Ahmed Ali Khan",
            phone = "+92 302 9876543",
            role = "BUYER"
        )

        db.userDao().insertUser(adminUser)
        db.userDao().insertUser(sellerUser1)
        db.userDao().insertUser(sellerUser2)
        db.userDao().insertUser(buyerUser)

        // 2. Initial Shops
        val shop1 = SellerShopEntity(
            id = "shop_techhub",
            sellerId = "user_seller_1",
            shopName = "TechHub Saddar",
            description = "Pakistan's verified distributor for premium electronics, smartphones, and genuine smart wearables.",
            phone = "+92 321 5551234",
            businessAddress = "Shop #45, Regal Trade Square, Saddar",
            city = "Karachi",
            status = "APPROVED",
            commissionRate = 0.05,
            rating = 4.9,
            totalSales = 248,
            earnings = 1250000.0
        )
        val shop2 = SellerShopEntity(
            id = "shop_fashionhub",
            sellerId = "user_seller_2",
            shopName = "Noor-e-Khaas Couture",
            description = "Designer stitched Eastern wear, festive lawn suits, and handcrafted Pakistani heritage garments.",
            phone = "+92 333 7778899",
            businessAddress = "12-C, MM Alam Road, Gulberg III",
            city = "Lahore",
            status = "APPROVED",
            commissionRate = 0.06,
            rating = 4.8,
            totalSales = 185,
            earnings = 780000.0
        )
        val shop3 = SellerShopEntity(
            id = "shop_sialkot",
            sellerId = "user_seller_3",
            shopName = "Sialkot Gear Masters",
            description = "Authentic cricket bats, footballs, and gym equipment exported directly from Sialkot.",
            phone = "+92 345 1122334",
            businessAddress = "Kashmir Road Industrial Area",
            city = "Sialkot",
            status = "APPROVED",
            commissionRate = 0.05,
            rating = 4.7,
            totalSales = 95,
            earnings = 450000.0
        )

        db.shopDao().insertShop(shop1)
        db.shopDao().insertShop(shop2)
        db.shopDao().insertShop(shop3)

        // 3. Initial 12 Categories
        val categories = listOf(
            CategoryEntity("cat_electronics", "Electronics", "Devices", "", 18),
            CategoryEntity("cat_mobiles", "Mobile Phones & Accessories", "Smartphone", "", 24),
            CategoryEntity("cat_fashion", "Fashion & Clothing", "Checkroom", "", 32),
            CategoryEntity("cat_shoes", "Shoes", "Hiking", "", 15),
            CategoryEntity("cat_beauty", "Beauty & Personal Care", "Spa", "", 20),
            CategoryEntity("cat_home", "Home & Living", "Home", "", 19),
            CategoryEntity("cat_furniture", "Furniture", "Chair", "", 12),
            CategoryEntity("cat_grocery", "Grocery", "ShoppingBasket", "", 45),
            CategoryEntity("cat_sports", "Sports & Outdoors", "SportsCricket", "", 16),
            CategoryEntity("cat_books", "Books & Stationery", "MenuBook", "", 14),
            CategoryEntity("cat_toys", "Toys & Games", "Gamepad", "", 11),
            CategoryEntity("cat_auto", "Automotive Accessories", "DirectionsCar", "", 10)
        )
        db.categoryDao().insertAll(categories)

        // 4. Products in Pakistani Rupees (PKR)
        val products = listOf(
            ProductEntity(
                id = "prod_s24_ultra",
                shopId = "shop_techhub",
                shopName = "TechHub Saddar",
                title = "Samsung Galaxy S24 Ultra 5G (12GB RAM, 256GB)",
                description = "Official PTA Approved Samsung Galaxy S24 Ultra with Snapdragon 8 Gen 3, Titanium Frame, 200MP Quad Camera, S-Pen included, and 1 Year Official Local Samsung Warranty across Pakistan.",
                category = "Mobile Phones & Accessories",
                subcategory = "Smartphones",
                price = 399999.0,
                discountPrice = 369999.0,
                stock = 8,
                variationsJson = "[\"Titanium Black\", \"Titanium Gray\", \"Titanium Violet\"]",
                rating = 4.9,
                reviewCount = 38,
                status = "APPROVED",
                isFlashDeal = true,
                isFeatured = true,
                brand = "Samsung",
                cityLocation = "Karachi"
            ),
            ProductEntity(
                id = "prod_infinix_note40",
                shopId = "shop_techhub",
                shopName = "TechHub Saddar",
                title = "Infinix Note 40 Pro 5G (8GB+256GB)",
                description = "108MP OIS Super-Zoom Camera, 120Hz 3D-Curved AMOLED Display, 45W All-Round FastCharge 2.0 with MagCharge support, PTA Approved with 1-year Carlcare warranty.",
                category = "Mobile Phones & Accessories",
                subcategory = "Smartphones",
                price = 74999.0,
                discountPrice = 67999.0,
                stock = 25,
                variationsJson = "[\"Vintage Green\", \"Titan Gold\"]",
                rating = 4.7,
                reviewCount = 54,
                status = "APPROVED",
                isFlashDeal = true,
                isFeatured = true,
                brand = "Infinix",
                cityLocation = "Karachi"
            ),
            ProductEntity(
                id = "prod_audionic_airbud",
                shopId = "shop_techhub",
                shopName = "TechHub Saddar",
                title = "Audionic Airbud 550 TWS Wireless Earbuds Quad Mic ENC",
                description = "Ultra low latency gaming mode, Quad Mic Environmental Noise Cancellation, 40 Hours playtime with fast charging case, deep bass tuned for Pakistani music.",
                category = "Electronics",
                subcategory = "Audio",
                price = 4999.0,
                discountPrice = 3499.0,
                stock = 45,
                variationsJson = "[\"Midnight Black\", \"Snow White\", \"Navy Blue\"]",
                rating = 4.6,
                reviewCount = 89,
                status = "APPROVED",
                isFlashDeal = true,
                isFeatured = false,
                brand = "Audionic",
                cityLocation = "Karachi"
            ),
            ProductEntity(
                id = "prod_mens_kurta",
                shopId = "shop_fashionhub",
                shopName = "Noor-e-Khaas Couture",
                title = "Men's Premium Wash & Wear Embroidered Shalwar Kameez",
                description = "Wrinkle-resistant fine tropical fabric, intricate ban collar thread embroidery, tailored cuffs, and premium matching shalwar. Ideal for Jummah, Eid, and formal gatherings.",
                category = "Fashion & Clothing",
                subcategory = "Men's Ethnic",
                price = 5500.0,
                discountPrice = 3850.0,
                stock = 30,
                variationsJson = "[\"Small\", \"Medium\", \"Large\", \"X-Large\"]",
                rating = 4.8,
                reviewCount = 42,
                status = "APPROVED",
                isFlashDeal = false,
                isFeatured = true,
                brand = "Noor-e-Khaas",
                cityLocation = "Lahore"
            ),
            ProductEntity(
                id = "prod_womens_lawn",
                shopId = "shop_fashionhub",
                shopName = "Noor-e-Khaas Couture",
                title = "Women's 3-Piece Stitched Embroidered Lawn Suit with Chiffon Dupatta",
                description = "Luxury digital printed and embroidered lawn shirt, digitally printed pure chiffon dupatta, and dyed cambric trousers. Ready to wear collection.",
                category = "Fashion & Clothing",
                subcategory = "Women's Stitched",
                price = 6800.0,
                discountPrice = 4999.0,
                stock = 20,
                variationsJson = "[\"Small\", \"Medium\", \"Large\"]",
                rating = 4.9,
                reviewCount = 67,
                status = "APPROVED",
                isFlashDeal = true,
                isFeatured = true,
                brand = "Noor-e-Khaas",
                cityLocation = "Lahore"
            ),
            ProductEntity(
                id = "prod_servis_cheetah",
                shopId = "shop_sialkot",
                shopName = "Sialkot Gear Masters",
                title = "Servis Cheetah Pro Athletic Training & Running Shoes",
                description = "Breathable mesh upper, cushioned EVA shock absorbing midsole, durable rubber traction outsole, lightweight comfortable everyday fitness sneaker.",
                category = "Shoes",
                subcategory = "Men's Athletic",
                price = 4500.0,
                discountPrice = 3299.0,
                stock = 18,
                variationsJson = "[\"Size 40 (7)\", \"Size 41 (8)\", \"Size 42 (9)\", \"Size 43 (10)\"]",
                rating = 4.6,
                reviewCount = 31,
                status = "APPROVED",
                isFlashDeal = false,
                isFeatured = false,
                brand = "Servis",
                cityLocation = "Lahore"
            ),
            ProductEntity(
                id = "prod_rivaj_makeup",
                shopId = "shop_fashionhub",
                shopName = "Noor-e-Khaas Couture",
                title = "Rivaj UK HD 18-Shade Eyeshadow & Velvet Matte Lip Kit",
                description = "Original Rivaj UK glam palette with high pigmentation, blendable silky shades, waterproof long-lasting formula, plus two matching liquid matte lip shades.",
                category = "Beauty & Personal Care",
                subcategory = "Makeup",
                price = 2800.0,
                discountPrice = 1950.0,
                stock = 50,
                variationsJson = "[\"Nude Rose Palette\", \"Sunset Bronze Palette\"]",
                rating = 4.7,
                reviewCount = 28,
                status = "APPROVED",
                isFlashDeal = false,
                isFeatured = true,
                brand = "Rivaj UK",
                cityLocation = "Karachi"
            ),
            ProductEntity(
                id = "prod_ca_cricket_bat",
                shopId = "shop_sialkot",
                shopName = "Sialkot Gear Masters",
                title = "CA Plus 15000 English Willow Cricket Bat Grade 1",
                description = "Handcrafted in Sialkot, Grade 1 unbleached English Willow, 7-9 straight grains, massive thick edges with dynamic sweet spot, factory knocked and toe-guarded.",
                category = "Sports & Outdoors",
                subcategory = "Cricket",
                price = 38000.0,
                discountPrice = 32500.0,
                stock = 7,
                variationsJson = "[\"Short Handle (2.8 lbs)\", \"Short Handle (2.9 lbs)\"]",
                rating = 5.0,
                reviewCount = 19,
                status = "APPROVED",
                isFlashDeal = false,
                isFeatured = true,
                brand = "CA Sports",
                cityLocation = "Sialkot"
            ),
            ProductEntity(
                id = "prod_shan_masala_pack",
                shopId = "shop_techhub",
                shopName = "TechHub Saddar",
                title = "Shan Mega Feast Recipe Masala Family Value Pack (Set of 6)",
                description = "Includes Special Bombay Biryani, Chicken Karahi, Nihari, Haleem, Seekh Kabab, and Tikka Masala. Authentic rich taste of traditional Pakistani delicacies.",
                category = "Grocery",
                subcategory = "Spices & Cooking",
                price = 1450.0,
                discountPrice = 1190.0,
                stock = 100,
                variationsJson = "[\"Family Pack (6 Boxes)\"]",
                rating = 4.9,
                reviewCount = 112,
                status = "APPROVED",
                isFlashDeal = true,
                isFeatured = false,
                brand = "Shan Foods",
                cityLocation = "Karachi"
            ),
            ProductEntity(
                id = "prod_chiniot_table",
                shopId = "shop_fashionhub",
                shopName = "Noor-e-Khaas Couture",
                title = "Chinioti Handcrafted Solid Sheesham Wood Carved Coffee Table",
                description = "Authentic Chiniot heritage craftsmanship, carved natural rosewood (Sheesham) with glass top inlay and natural polish finish. Durable heirloom furniture.",
                category = "Furniture",
                subcategory = "Living Room",
                price = 24000.0,
                discountPrice = 18999.0,
                stock = 4,
                variationsJson = "[\"Walnut Brown\", \"Dark Teak\"]",
                rating = 4.8,
                reviewCount = 15,
                status = "APPROVED",
                isFlashDeal = false,
                isFeatured = true,
                brand = "Chiniot Art",
                cityLocation = "Chiniot"
            ),
            ProductEntity(
                id = "prod_urdu_dictionary",
                shopId = "shop_sialkot",
                shopName = "Sialkot Gear Masters",
                title = "Feroz-ul-Lughat Comprehensive Urdu-to-Urdu & English Edition",
                description = "The gold standard Urdu lexicon containing over 100,000 words, idioms, proverbs, and scientific terms. Deluxe hardcover binding.",
                category = "Books & Stationery",
                subcategory = "Reference",
                price = 3200.0,
                discountPrice = 2490.0,
                stock = 22,
                variationsJson = "[\"Standard Hardcover\"]",
                rating = 4.9,
                reviewCount = 23,
                status = "APPROVED",
                isFlashDeal = false,
                isFeatured = false,
                brand = "Ferozsons",
                cityLocation = "Lahore"
            ),
            ProductEntity(
                id = "prod_car_charger",
                shopId = "shop_techhub",
                shopName = "TechHub Saddar",
                title = "Baseus 65W Dual Port Car Fast Charger (USB-C PD + QuickCharge 4.0)",
                description = "Intelligent voltage regulation with real-time digital LED display. Charges laptops, MacBooks, tablets, and phones at top speeds while commuting.",
                category = "Automotive Accessories",
                subcategory = "Car Electronics",
                price = 2800.0,
                discountPrice = 1899.0,
                stock = 35,
                variationsJson = "[\"Space Gray Metallic\"]",
                rating = 4.7,
                reviewCount = 47,
                status = "APPROVED",
                isFlashDeal = true,
                isFeatured = false,
                brand = "Baseus",
                cityLocation = "Islamabad"
            )
        )
        db.productDao().insertAll(products)

        // 5. Coupons
        val coupons = listOf(
            CouponEntity("coup_1", "AZADI70", 15, 2000.0, 4000.0, true),
            CouponEntity("coup_2", "BICHKAND", 10, 1000.0, 2000.0, true),
            CouponEntity("coup_3", "WELCOME10", 10, 500.0, 1500.0, true)
        )
        coupons.forEach { db.couponDao().insertCoupon(it) }

        // 6. User Address
        val address1 = AddressEntity(
            id = "addr_1",
            userId = "user_buyer_1",
            label = "Home",
            recipientName = "Ahmed Ali Khan",
            phone = "+92 302 9876543",
            streetAddress = "House 14-B, Sector J, Phase 5, DHA",
            city = "Lahore",
            province = "Punjab",
            isDefault = true
        )
        val address2 = AddressEntity(
            id = "addr_2",
            userId = "user_buyer_1",
            label = "Office",
            recipientName = "Ahmed Ali Khan",
            phone = "+92 302 9876543",
            streetAddress = "Level 4, Arfa Software Technology Park, Ferozepur Road",
            city = "Lahore",
            province = "Punjab",
            isDefault = false
        )
        db.addressDao().insertAddress(address1)
        db.addressDao().insertAddress(address2)

        // 7. Seed Past Order for Buyer
        val pastOrder = OrderEntity(
            id = "ord_bk_10928",
            orderNumber = "BK-2024-9182",
            buyerId = "user_buyer_1",
            buyerName = "Ahmed Ali Khan",
            buyerPhone = "+92 302 9876543",
            deliveryAddress = "House 14-B, Sector J, Phase 5, DHA, Lahore",
            city = "Lahore",
            paymentMethod = "COD",
            paymentStatus = "PAID",
            orderStatus = "DELIVERED",
            subtotal = 3499.0,
            deliveryFee = 199.0,
            discountAmount = 350.0,
            totalAmount = 3348.0,
            couponCode = "WELCOME10",
            itemsJson = "[{\"productId\":\"prod_audionic_airbud\",\"shopId\":\"shop_techhub\",\"shopName\":\"TechHub Saddar\",\"title\":\"Audionic Airbud 550 TWS Wireless Earbuds Quad Mic ENC\",\"price\":3499.0,\"quantity\":1,\"variation\":\"Midnight Black\",\"imageUrl\":\"\"}]",
            sellerIdsJson = "[\"shop_techhub\"]",
            createdAt = System.currentTimeMillis() - 86400000L * 4
        )
        db.orderDao().insertOrder(pastOrder)

        // 8. Reviews
        val review1 = ReviewEntity(
            id = "rev_1",
            productId = "prod_s24_ultra",
            userId = "user_buyer_1",
            userName = "Ahmed Ali Khan",
            rating = 5,
            comment = "100% genuine PTA approved phone. Packaging was sealed with warranty card. Super fast delivery in Lahore!",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )
        val review2 = ReviewEntity(
            id = "rev_2",
            productId = "prod_audionic_airbud",
            userId = "user_buyer_1",
            userName = "Hamza Tariq",
            rating = 4,
            comment = "Great bass and very clear voice on calls. Battery lasts for two full days with the case.",
            createdAt = System.currentTimeMillis() - 86400000L * 3
        )
        db.reviewDao().insertReview(review1)
        db.reviewDao().insertReview(review2)

        // 9. Platform Settings
        db.platformDao().saveSettings(
            PlatformSettingsEntity(
                id = "default",
                commissionPercent = 5.0,
                baseDeliveryFee = 199.0,
                announcementText = "BICHKAND Azadi Mega Deals are LIVE! Enjoy up to 70% off with fast delivery across Pakistan!"
            )
        )
    }
}
