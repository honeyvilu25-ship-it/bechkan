package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CouponResult
import com.example.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class Search(val initialQuery: String = "", val initialCategory: String = "") : Screen()
    data class ProductDetail(val productId: String) : Screen()
    object Cart : Screen()
    object Checkout : Screen()
    data class OrderSuccess(val orderId: String) : Screen()
    data class OrderTracking(val orderId: String) : Screen()
    object OrdersHistory : Screen()
    object Wishlist : Screen()
    object Profile : Screen()
    object SellerDashboard : Screen()
    data class SellerAddEditProduct(val productId: String? = null) : Screen()
    object SellerRegistration : Screen()
    object AdminDashboard : Screen()
    data class ShopDetail(val shopId: String) : Screen()
}

class MarketplaceViewModel(
    private val repository: MarketplaceRepository
) : ViewModel() {

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Home)

    fun navigateTo(screen: Screen) {
        _screenStack.update { it + screen }
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.update { it.dropLast(1) }
            return true
        }
        return false
    }

    fun navigateToHome() {
        _screenStack.value = listOf(Screen.Home)
    }

    // User & Auth
    val currentUser = repository.currentUser
    val selectedCity = repository.selectedCity

    fun setCity(city: String) {
        repository.setCity(city)
    }

    fun switchRole(role: String) {
        viewModelScope.launch {
            repository.switchUserRole(role)
        }
    }

    // Data streams
    val categories = repository.categories
    val approvedProducts = repository.approvedProducts
    val flashDeals = repository.flashDeals
    val featuredProducts = repository.featuredProducts
    val approvedShops = repository.approvedShops
    val allShops = repository.allShops

    fun getProductById(id: String): Flow<ProductEntity?> = repository.getProductById(id)

    // Cart
    val cartItems = currentUser.flatMapLatest { user ->
        if (user != null) repository.getCartItems(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount = cartItems.map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal = cartItems.map { items -> items.sumOf { it.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Applied Coupon
    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon = _appliedCoupon.asStateFlow()

    private val _couponDiscount = MutableStateFlow(0.0)
    val couponDiscount = _couponDiscount.asStateFlow()

    private val _couponError = MutableStateFlow<String?>(null)
    val couponError = _couponError.asStateFlow()

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            _couponError.value = null
            when (val result = repository.validateCoupon(code, cartSubtotal.value)) {
                is CouponResult.Success -> {
                    _appliedCoupon.value = result.coupon
                    _couponDiscount.value = result.discountAmount
                }
                is CouponResult.Error -> {
                    _couponError.value = result.message
                    _appliedCoupon.value = null
                    _couponDiscount.value = 0.0
                }
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponDiscount.value = 0.0
        _couponError.value = null
    }

    fun addToCart(product: ProductEntity, quantity: Int = 1, variation: String = "Standard") {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addToCart(user.id, product, quantity, variation)
        }
    }

    fun updateCartQuantity(item: CartItemEntity, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(item, newQuantity)
        }
    }

    fun removeFromCart(item: CartItemEntity) {
        viewModelScope.launch {
            repository.removeFromCart(item)
        }
    }

    // Wishlist
    val wishlistItems = currentUser.flatMapLatest { user ->
        if (user != null) repository.getWishlist(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleWishlist(product: ProductEntity) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleWishlist(user.id, product)
        }
    }

    fun isProductInWishlist(productId: String): Flow<Boolean> {
        val user = currentUser.value ?: return flowOf(false)
        return repository.isInWishlist(user.id, productId)
    }

    // Orders
    val buyerOrders = currentUser.flatMapLatest { user ->
        if (user != null) repository.getBuyerOrders(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getOrderFlow(orderId: String) = repository.getOrderById(orderId)

    // Addresses
    val userAddresses = currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserAddresses(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAddress(recipientName: String, phone: String, street: String, city: String, province: String, label: String, isDefault: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val addr = AddressEntity(
                id = "addr_${System.currentTimeMillis()}",
                userId = user.id,
                label = label,
                recipientName = recipientName,
                phone = phone,
                streetAddress = street,
                city = city,
                province = province,
                isDefault = isDefault
            )
            repository.addAddress(addr)
        }
    }

    // Place Order
    fun placeOrder(
        deliveryAddress: String,
        city: String,
        paymentMethod: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        val user = currentUser.value ?: return
        val items = cartItems.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            val deliveryFee = if (cartSubtotal.value > 2500) 0.0 else 199.0
            val order = repository.placeOrder(
                buyer = user,
                items = items,
                deliveryAddress = deliveryAddress,
                city = city,
                paymentMethod = paymentMethod,
                couponCode = _appliedCoupon.value?.code,
                discountAmount = _couponDiscount.value,
                deliveryFee = deliveryFee
            )
            removeCoupon()
            onSuccess(order)
        }
    }

    // Seller Specific
    val sellerShop = currentUser.flatMapLatest { user ->
        if (user != null && user.role == "SELLER") {
            repository.getSellerShopFlow(user.id)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sellerProducts = sellerShop.flatMapLatest { shop ->
        if (shop != null) repository.getProductsByShop(shop.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registerSeller(
        fullName: String,
        shopName: String,
        email: String,
        phone: String,
        password: String,
        description: String,
        address: String,
        city: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.registerSeller(
                fullName = fullName,
                shopName = shopName,
                email = email,
                phone = phone,
                password = password,
                shopDescription = description,
                businessAddress = address,
                city = city
            )
            if (res.isSuccess) {
                onResult(true, "Seller account registered! Your shop is pending approval by BICHKAND Admin.")
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun saveProduct(
        existingId: String?,
        title: String,
        category: String,
        subcategory: String,
        price: Double,
        discountPrice: Double?,
        stock: Int,
        variations: List<String>,
        description: String,
        brand: String,
        onSuccess: () -> Unit
    ) {
        val shop = sellerShop.value ?: return
        viewModelScope.launch {
            val variationsJson = variations.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
            if (existingId != null) {
                val existing = repository.getProductDirect(existingId)
                if (existing != null) {
                    val updated = existing.copy(
                        title = title,
                        category = category,
                        subcategory = subcategory,
                        price = price,
                        discountPrice = discountPrice,
                        stock = stock,
                        variationsJson = variationsJson,
                        description = description,
                        brand = brand
                    )
                    repository.updateProduct(updated)
                }
            } else {
                val newProduct = ProductEntity(
                    id = "prod_${System.currentTimeMillis()}",
                    shopId = shop.id,
                    shopName = shop.shopName,
                    title = title,
                    description = description,
                    category = category,
                    subcategory = subcategory,
                    price = price,
                    discountPrice = discountPrice,
                    stock = stock,
                    variationsJson = variationsJson,
                    brand = brand,
                    cityLocation = shop.city,
                    status = if (shop.status == "APPROVED") "APPROVED" else "PENDING"
                )
                repository.addProduct(newProduct)
            }
            onSuccess()
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    // Admin Operations
    val allProductsAdmin = repository.allProductsAdmin
    val allOrdersAdmin = repository.allOrdersAdmin
    val allComplaints = repository.allComplaints
    val totalUsers = repository.totalUsersCount
    val totalShops = repository.totalShopsCount
    val totalProducts = repository.totalProductsCount
    val totalOrders = repository.totalOrdersCount
    val totalRevenue = repository.totalRevenue
    val platformSettings = repository.platformSettings

    fun approveShop(shopId: String) {
        viewModelScope.launch {
            repository.updateShopStatus(shopId, "APPROVED")
        }
    }

    fun rejectShop(shopId: String) {
        viewModelScope.launch {
            repository.updateShopStatus(shopId, "REJECTED")
        }
    }

    fun setProductStatus(product: ProductEntity, status: String) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(status = status))
        }
    }

    fun addCategory(name: String, iconName: String) {
        viewModelScope.launch {
            val cat = CategoryEntity(
                id = "cat_${System.currentTimeMillis()}",
                name = name.trim(),
                iconName = iconName,
                productCount = 0
            )
            repository.addCategory(cat)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun updateComplaint(complaintId: String, status: String, notes: String) {
        viewModelScope.launch {
            repository.updateComplaintStatus(complaintId, status, notes)
        }
    }

    fun updateCommissionRate(rate: Double, baseDeliveryFee: Double, announcement: String) {
        viewModelScope.launch {
            repository.updatePlatformSettings(
                PlatformSettingsEntity(
                    id = "default",
                    commissionPercent = rate,
                    baseDeliveryFee = baseDeliveryFee,
                    announcementText = announcement
                )
            )
        }
    }

    // Reviews
    fun getProductReviews(productId: String) = repository.getProductReviews(productId)

    fun addReview(productId: String, rating: Int, comment: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addReview(productId, user, rating, comment)
        }
    }
}
