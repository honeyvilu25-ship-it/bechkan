package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MarketplaceRepository(
    private val db: AppDatabase,
    private val appScope: CoroutineScope
) {
    // Current active logged in user
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Location / City filter
    private val _selectedCity = MutableStateFlow("All Pakistan")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    init {
        appScope.launch(Dispatchers.IO) {
            DatabaseSeeder.checkAndSeedIfEmpty(db)
            // Default logged in as buyer for initial seamless browsing
            val buyer = db.userDao().getUserById("user_buyer_1")
            _currentUser.value = buyer
        }
    }

    fun setCity(city: String) {
        _selectedCity.value = city
    }

    // --- Authentication & Role Switching ---
    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserByEmail(email.trim())
        if (user != null && user.passwordHash == password.trim()) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password."))
        }
    }

    suspend fun registerBuyer(name: String, email: String, password: String, phone: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = db.userDao().getUserByEmail(email.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }
        val newUser = UserEntity(
            id = "user_${UUID.randomUUID().toString().take(8)}",
            email = email.trim(),
            passwordHash = password.trim(),
            name = name.trim(),
            phone = phone.trim(),
            role = "BUYER"
        )
        db.userDao().insertUser(newUser)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun registerSeller(
        fullName: String,
        shopName: String,
        email: String,
        phone: String,
        password: String,
        shopDescription: String,
        businessAddress: String,
        city: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = db.userDao().getUserByEmail(email.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }
        val userId = "user_${UUID.randomUUID().toString().take(8)}"
        val newUser = UserEntity(
            id = userId,
            email = email.trim(),
            passwordHash = password.trim(),
            name = fullName.trim(),
            phone = phone.trim(),
            role = "SELLER"
        )
        val shopId = "shop_${UUID.randomUUID().toString().take(8)}"
        val newShop = SellerShopEntity(
            id = shopId,
            sellerId = userId,
            shopName = shopName.trim(),
            description = shopDescription.trim(),
            phone = phone.trim(),
            businessAddress = businessAddress.trim(),
            city = city.trim(),
            status = "PENDING" // Requires Admin Approval
        )
        db.userDao().insertUser(newUser)
        db.shopDao().insertShop(newShop)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun switchUserRole(role: String) = withContext(Dispatchers.IO) {
        val targetUser = when (role) {
            "ADMIN" -> db.userDao().getUserById("user_admin_1")
            "SELLER" -> db.userDao().getUserById("user_seller_1")
            else -> db.userDao().getUserById("user_buyer_1")
        }
        if (targetUser != null) {
            _currentUser.value = targetUser
        }
    }

    fun logout() {
        // Switch back to buyer account for marketplace browsing
        appScope.launch(Dispatchers.IO) {
            _currentUser.value = db.userDao().getUserById("user_buyer_1")
        }
    }

    // --- Products ---
    val approvedProducts: Flow<List<ProductEntity>> = db.productDao().getAllApprovedProductsFlow()
    val allProductsAdmin: Flow<List<ProductEntity>> = db.productDao().getAllProductsAdminFlow()
    val flashDeals: Flow<List<ProductEntity>> = db.productDao().getFlashDealsFlow()
    val featuredProducts: Flow<List<ProductEntity>> = db.productDao().getFeaturedProductsFlow()

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> =
        db.productDao().getProductsByCategoryFlow(category)

    fun searchProducts(query: String): Flow<List<ProductEntity>> =
        db.productDao().searchProductsFlow(query)

    fun getProductById(id: String): Flow<ProductEntity?> =
        db.productDao().getProductByIdFlow(id)

    suspend fun getProductDirect(id: String): ProductEntity? = withContext(Dispatchers.IO) {
        db.productDao().getProductById(id)
    }

    fun getProductsByShop(shopId: String): Flow<List<ProductEntity>> =
        db.productDao().getProductsByShopFlow(shopId)

    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().deleteProduct(product)
    }

    // --- Categories ---
    val categories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategoriesFlow()

    suspend fun addCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        db.categoryDao().insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        db.categoryDao().deleteCategory(category)
    }

    // --- Shops ---
    val allShops: Flow<List<SellerShopEntity>> = db.shopDao().getAllShopsFlow()
    val approvedShops: Flow<List<SellerShopEntity>> = db.shopDao().getApprovedShopsFlow()

    fun getSellerShopFlow(sellerId: String): Flow<SellerShopEntity?> =
        db.shopDao().getShopBySellerIdFlow(sellerId)

    suspend fun updateShopStatus(shopId: String, newStatus: String) = withContext(Dispatchers.IO) {
        val shop = db.shopDao().getShopById(shopId)
        if (shop != null) {
            db.shopDao().updateShop(shop.copy(status = newStatus))
        }
    }

    suspend fun updateShopDetails(shop: SellerShopEntity) = withContext(Dispatchers.IO) {
        db.shopDao().updateShop(shop)
    }

    // --- Cart ---
    fun getCartItems(userId: String): Flow<List<CartItemEntity>> =
        db.cartDao().getCartItemsFlow(userId)

    suspend fun addToCart(userId: String, product: ProductEntity, quantity: Int = 1, variation: String = "Standard") = withContext(Dispatchers.IO) {
        val cartItemId = "cart_${userId}_${product.id}_${variation.replace(" ", "_")}"
        val item = CartItemEntity(
            id = cartItemId,
            userId = userId,
            productId = product.id,
            title = product.title,
            price = product.displayPrice,
            imageUrl = "",
            shopName = product.shopName,
            quantity = quantity,
            selectedVariation = variation
        )
        db.cartDao().insertCartItem(item)
    }

    suspend fun updateCartQuantity(item: CartItemEntity, newQuantity: Int) = withContext(Dispatchers.IO) {
        if (newQuantity <= 0) {
            db.cartDao().deleteCartItem(item)
        } else {
            db.cartDao().updateCartItem(item.copy(quantity = newQuantity))
        }
    }

    suspend fun removeFromCart(item: CartItemEntity) = withContext(Dispatchers.IO) {
        db.cartDao().deleteCartItem(item)
    }

    suspend fun clearCart(userId: String) = withContext(Dispatchers.IO) {
        db.cartDao().clearCart(userId)
    }

    // --- Wishlist ---
    fun getWishlist(userId: String): Flow<List<WishlistItemEntity>> =
        db.wishlistDao().getWishlistFlow(userId)

    fun isInWishlist(userId: String, productId: String): Flow<Boolean> =
        db.wishlistDao().isInWishlistFlow(userId, productId)

    suspend fun toggleWishlist(userId: String, product: ProductEntity) = withContext(Dispatchers.IO) {
        val wishlistId = "wish_${userId}_${product.id}"
        val exists = db.wishlistDao().getWishlistFlow(userId).first().any { it.productId == product.id }
        if (exists) {
            db.wishlistDao().removeWishlistItem(userId, product.id)
        } else {
            val item = WishlistItemEntity(
                id = wishlistId,
                userId = userId,
                productId = product.id,
                title = product.title,
                price = product.displayPrice,
                imageUrl = "",
                category = product.category
            )
            db.wishlistDao().insertWishlistItem(item)
        }
    }

    // --- Orders ---
    val allOrdersAdmin: Flow<List<OrderEntity>> = db.orderDao().getAllOrdersFlow()

    fun getBuyerOrders(buyerId: String): Flow<List<OrderEntity>> =
        db.orderDao().getOrdersByBuyerFlow(buyerId)

    fun getOrderById(orderId: String): Flow<OrderEntity?> =
        db.orderDao().getOrderByIdFlow(orderId)

    suspend fun placeOrder(
        buyer: UserEntity,
        items: List<CartItemEntity>,
        deliveryAddress: String,
        city: String,
        paymentMethod: String,
        couponCode: String?,
        discountAmount: Double,
        deliveryFee: Double
    ): OrderEntity = withContext(Dispatchers.IO) {
        val subtotal = items.sumOf { it.price * it.quantity }
        val total = (subtotal + deliveryFee - discountAmount).coerceAtLeast(0.0)
        val orderNum = "BK-${(100000..999999).random()}"
        val orderId = "order_${UUID.randomUUID().toString().take(8)}"

        // Build items json
        val itemsDataJson = items.joinToString(prefix = "[", postfix = "]") { item ->
            "{\"productId\":\"${item.productId}\",\"shopId\":\"\",\"shopName\":\"${item.shopName}\",\"title\":\"${item.title.replace("\"", "\\\"")}\",\"price\":${item.price},\"quantity\":${item.quantity},\"variation\":\"${item.selectedVariation}\",\"imageUrl\":\"\"}"
        }

        val order = OrderEntity(
            id = orderId,
            orderNumber = orderNum,
            buyerId = buyer.id,
            buyerName = buyer.name,
            buyerPhone = buyer.phone,
            deliveryAddress = deliveryAddress,
            city = city,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "COD") "PENDING" else "PAID (TEST)",
            orderStatus = "PENDING",
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discountAmount = discountAmount,
            totalAmount = total,
            couponCode = couponCode,
            itemsJson = itemsDataJson,
            createdAt = System.currentTimeMillis()
        )
        db.orderDao().insertOrder(order)
        db.cartDao().clearCart(buyer.id)
        order
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) = withContext(Dispatchers.IO) {
        val order = db.orderDao().getOrderById(orderId)
        if (order != null) {
            val updated = order.copy(
                orderStatus = newStatus,
                paymentStatus = if (newStatus == "DELIVERED" && order.paymentMethod == "COD") "PAID" else order.paymentStatus,
                updatedAt = System.currentTimeMillis()
            )
            db.orderDao().updateOrder(updated)
        }
    }

    // --- Reviews ---
    fun getProductReviews(productId: String): Flow<List<ReviewEntity>> =
        db.reviewDao().getReviewsForProductFlow(productId)

    suspend fun addReview(productId: String, user: UserEntity, rating: Int, comment: String) = withContext(Dispatchers.IO) {
        val review = ReviewEntity(
            id = "rev_${UUID.randomUUID().toString().take(8)}",
            productId = productId,
            userId = user.id,
            userName = user.name,
            rating = rating,
            comment = comment.trim()
        )
        db.reviewDao().insertReview(review)

        // Update product rating average
        val product = db.productDao().getProductById(productId)
        if (product != null) {
            val newCount = product.reviewCount + 1
            val newRating = ((product.rating * product.reviewCount) + rating) / newCount
            db.productDao().updateProduct(
                product.copy(
                    rating = (Math.round(newRating * 10.0) / 10.0),
                    reviewCount = newCount
                )
            )
        }
    }

    // --- Addresses ---
    fun getUserAddresses(userId: String): Flow<List<AddressEntity>> =
        db.addressDao().getAddressesFlow(userId)

    suspend fun addAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        if (address.isDefault) {
            db.addressDao().clearDefaults(address.userId)
        }
        db.addressDao().insertAddress(address)
    }

    suspend fun deleteAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        db.addressDao().deleteAddress(address)
    }

    // --- Coupons ---
    val activeCoupons: Flow<List<CouponEntity>> = db.couponDao().getActiveCouponsFlow()

    suspend fun validateCoupon(code: String, subtotal: Double): CouponResult = withContext(Dispatchers.IO) {
        val coupon = db.couponDao().getCouponByCode(code.trim().uppercase())
        if (coupon == null) {
            return@withContext CouponResult.Error("Invalid or expired coupon code")
        }
        if (subtotal < coupon.minOrder) {
            return@withContext CouponResult.Error("Minimum order of Rs. ${coupon.minOrder.toInt()} required for this coupon")
        }
        val discount = ((subtotal * coupon.discountPercent) / 100.0).coerceAtMost(coupon.maxDiscount)
        CouponResult.Success(coupon, discount)
    }

    // --- Complaints / Disputes ---
    val allComplaints: Flow<List<ComplaintEntity>> = db.complaintDao().getAllComplaintsFlow()

    suspend fun submitComplaint(orderId: String, user: UserEntity, subject: String, description: String) = withContext(Dispatchers.IO) {
        val complaint = ComplaintEntity(
            id = "comp_${UUID.randomUUID().toString().take(8)}",
            orderId = orderId,
            userId = user.id,
            userName = user.name,
            subject = subject.trim(),
            description = description.trim(),
            status = "OPEN"
        )
        db.complaintDao().insertComplaint(complaint)
    }

    suspend fun updateComplaintStatus(complaintId: String, status: String, adminNotes: String) = withContext(Dispatchers.IO) {
        val complaints = db.complaintDao().getAllComplaintsFlow().first()
        val match = complaints.find { it.id == complaintId }
        if (match != null) {
            db.complaintDao().updateComplaint(match.copy(status = status, adminNotes = adminNotes))
        }
    }

    // --- Platform & Analytics ---
    val totalUsersCount: Flow<Int> = db.userDao().getUserCountFlow()
    val totalShopsCount: Flow<Int> = db.shopDao().getShopCountFlow()
    val totalProductsCount: Flow<Int> = db.productDao().getProductCountFlow()
    val totalOrdersCount: Flow<Int> = db.orderDao().getOrderCountFlow()
    val totalRevenue: Flow<Double?> = db.orderDao().getTotalRevenueFlow()
    val platformSettings: Flow<PlatformSettingsEntity?> = db.platformDao().getSettingsFlow()

    suspend fun updatePlatformSettings(settings: PlatformSettingsEntity) = withContext(Dispatchers.IO) {
        db.platformDao().saveSettings(settings)
    }
}

sealed class CouponResult {
    data class Success(val coupon: CouponEntity, val discountAmount: Double) : CouponResult()
    data class Error(val message: String) : CouponResult()
}
