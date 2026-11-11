package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.model.ProductEntity
import com.example.data.repository.MarketplaceRepository
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.buyer.*
import com.example.ui.components.*
import com.example.ui.seller.*
import com.example.ui.theme.BichkandTheme
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: MarketplaceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this, lifecycleScope)
        repository = MarketplaceRepository(database, lifecycleScope)

        setContent {
            BichkandTheme {
                val viewModel: MarketplaceViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return MarketplaceViewModel(repository) as T
                        }
                    }
                )

                BichkandApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BichkandApp(viewModel: MarketplaceViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartCount.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle(emptyList())
    val approvedProducts by viewModel.approvedProducts.collectAsStateWithLifecycle(emptyList())
    val flashDeals by viewModel.flashDeals.collectAsStateWithLifecycle(emptyList())
    val featuredProducts by viewModel.featuredProducts.collectAsStateWithLifecycle(emptyList())
    val approvedShops by viewModel.approvedShops.collectAsStateWithLifecycle(emptyList())
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val couponDiscount by viewModel.couponDiscount.collectAsStateWithLifecycle()
    val couponError by viewModel.couponError.collectAsStateWithLifecycle()
    val buyerOrders by viewModel.buyerOrders.collectAsStateWithLifecycle()
    val userAddresses by viewModel.userAddresses.collectAsStateWithLifecycle()
    val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showCitySelector by remember { mutableStateOf(false) }
    var showRoleSwitcher by remember { mutableStateOf(false) }

    // System Back Handler
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    // Determine active bottom bar route
    val bottomRoute = when (currentScreen) {
        is Screen.Home -> "home"
        is Screen.Search -> "categories"
        is Screen.OrdersHistory, is Screen.OrderTracking -> "orders"
        is Screen.Cart -> "cart"
        is Screen.Profile, is Screen.SellerDashboard, is Screen.AdminDashboard -> "profile"
        else -> "home"
    }

    // Whether to show bottom nav
    val showBottomNav = currentScreen is Screen.Home ||
            currentScreen is Screen.Search ||
            currentScreen is Screen.OrdersHistory ||
            currentScreen is Screen.Cart ||
            currentScreen is Screen.Profile

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen is Screen.Home) {
                BichkandTopBar(
                    currentUser = currentUser,
                    selectedCity = selectedCity,
                    cartCount = cartCount,
                    onCityClick = { showCitySelector = true },
                    onSearchClick = { viewModel.navigateTo(Screen.Search()) },
                    onCartClick = { viewModel.navigateTo(Screen.Cart) },
                    onWishlistClick = { viewModel.navigateTo(Screen.Wishlist) },
                    onRoleBadgeClick = { showRoleSwitcher = true }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                BichkandBottomBar(
                    currentRoute = bottomRoute,
                    onNavigate = { route ->
                        when (route) {
                            "home" -> viewModel.navigateToHome()
                            "categories" -> viewModel.navigateTo(Screen.Search())
                            "orders" -> viewModel.navigateTo(Screen.OrdersHistory)
                            "cart" -> viewModel.navigateTo(Screen.Cart)
                            "profile" -> viewModel.navigateTo(Screen.Profile)
                        }
                    },
                    userRole = currentUser?.role
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    BuyerHomeScreen(
                        categories = categories,
                        flashDeals = flashDeals,
                        featuredProducts = featuredProducts,
                        allProducts = approvedProducts,
                        topShops = approvedShops,
                        selectedCity = selectedCity,
                        onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                        onCategoryClick = { catName -> viewModel.navigateTo(Screen.Search(initialCategory = catName)) },
                        onAddToCart = { prod ->
                            viewModel.addToCart(prod)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Added ${prod.title.take(24)}... to cart!")
                            }
                        },
                        onViewAllClick = { type ->
                            when (type) {
                                "categories" -> viewModel.navigateTo(Screen.Search())
                                "flash" -> viewModel.navigateTo(Screen.Search(initialQuery = "Deal"))
                                "featured" -> viewModel.navigateTo(Screen.Search())
                                "shops" -> viewModel.navigateTo(Screen.Profile)
                            }
                        },
                        onShopClick = { shopId -> viewModel.navigateTo(Screen.ShopDetail(shopId)) },
                        onOpenSearch = { viewModel.navigateTo(Screen.Search()) }
                    )
                }

                is Screen.Search -> {
                    SearchScreen(
                        initialQuery = screen.initialQuery,
                        initialCategory = screen.initialCategory,
                        allProducts = approvedProducts,
                        categories = categories,
                        onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                        onAddToCart = { prod ->
                            viewModel.addToCart(prod)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Added to cart!")
                            }
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.ProductDetail -> {
                    val productState = viewModel.getProductById(screen.productId).collectAsStateWithLifecycle(null)
                    val reviewsState = viewModel.getProductReviews(screen.productId).collectAsStateWithLifecycle(emptyList())
                    val inWishlistState = viewModel.isProductInWishlist(screen.productId).collectAsStateWithLifecycle(false)

                    val prod: ProductEntity? = productState.value
                    if (prod != null) {
                        val related = approvedProducts.filter { other -> other.category == prod.category && other.id != prod.id }
                        ProductDetailScreen(
                            product = prod,
                            reviews = reviewsState.value,
                            relatedProducts = related,
                            isInWishlist = inWishlistState.value,
                            onAddToCart = { qty, variation ->
                                viewModel.addToCart(prod, qty, variation)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Added $qty item(s) to Cart!")
                                }
                            },
                            onBuyNow = { qty, variation ->
                                viewModel.addToCart(prod, qty, variation)
                                viewModel.navigateTo(Screen.Checkout)
                            },
                            onToggleWishlist = {
                                viewModel.toggleWishlist(prod)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (inWishlistState.value) "Removed from Wishlist" else "Saved to Wishlist!"
                                    )
                                }
                            },
                            onAddReview = { rating, comment ->
                                viewModel.addReview(prod.id, rating, comment)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Review submitted. Thank you!")
                                }
                            },
                            onShopClick = { shopId -> viewModel.navigateTo(Screen.ShopDetail(shopId)) },
                            onRelatedProductClick = { relId -> viewModel.navigateTo(Screen.ProductDetail(relId)) },
                            onBack = { viewModel.navigateBack() }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is Screen.Cart -> {
                    CartScreen(
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        appliedCoupon = appliedCoupon,
                        couponDiscount = couponDiscount,
                        couponError = couponError,
                        onUpdateQuantity = { item, newQty -> viewModel.updateCartQuantity(item, newQty) },
                        onRemoveItem = { item -> viewModel.removeFromCart(item) },
                        onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                        onRemoveCoupon = { viewModel.removeCoupon() },
                        onProceedToCheckout = { viewModel.navigateTo(Screen.Checkout) },
                        onContinueShopping = { viewModel.navigateToHome() }
                    )
                }

                is Screen.Checkout -> {
                    CheckoutScreen(
                        addresses = userAddresses,
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        couponDiscount = couponDiscount,
                        couponCode = appliedCoupon?.code,
                        onAddAddress = { name, phone, street, city, prov, lbl, isDef ->
                            viewModel.addAddress(name, phone, street, city, prov, lbl, isDef)
                        },
                        onPlaceOrder = { address, city, paymentMethod ->
                            viewModel.placeOrder(
                                deliveryAddress = address,
                                city = city,
                                paymentMethod = paymentMethod,
                                onSuccess = { placedOrder ->
                                    viewModel.navigateTo(Screen.OrderSuccess(placedOrder.id))
                                }
                            )
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.OrderSuccess -> {
                    val orderState = viewModel.getOrderFlow(screen.orderId).collectAsStateWithLifecycle(null)
                    val order = orderState.value
                    if (order != null) {
                        OrderSuccessScreen(
                            order = order,
                            onTrackOrder = { ordId -> viewModel.navigateTo(Screen.OrderTracking(ordId)) },
                            onContinueShopping = { viewModel.navigateToHome() }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is Screen.OrderTracking -> {
                    val orderState = viewModel.getOrderFlow(screen.orderId).collectAsStateWithLifecycle(null)
                    val order = orderState.value
                    if (order != null) {
                        OrderTrackingScreen(
                            order = order,
                            onBack = { viewModel.navigateBack() }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is Screen.OrdersHistory -> {
                    OrdersHistoryScreen(
                        orders = buyerOrders,
                        onTrackOrder = { ordId -> viewModel.navigateTo(Screen.OrderTracking(ordId)) },
                        onStartShopping = { viewModel.navigateToHome() }
                    )
                }

                is Screen.Wishlist -> {
                    WishlistScreen(
                        wishlistItems = wishlistItems,
                        onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                        onRemoveFromWishlist = { prodId ->
                            val match = approvedProducts.find { it.id == prodId }
                            if (match != null) viewModel.toggleWishlist(match)
                        },
                        onStartShopping = { viewModel.navigateToHome() }
                    )
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        currentUser = currentUser,
                        addresses = userAddresses,
                        onOpenRoleSwitcher = { showRoleSwitcher = true },
                        onOpenSellerRegistration = { viewModel.navigateTo(Screen.SellerRegistration) },
                        onOpenSellerDashboard = { viewModel.navigateTo(Screen.SellerDashboard) },
                        onOpenAdminDashboard = { viewModel.navigateTo(Screen.AdminDashboard) },
                        onOpenOrdersHistory = { viewModel.navigateTo(Screen.OrdersHistory) }
                    )
                }

                is Screen.SellerDashboard -> {
                    val sellerShop by viewModel.sellerShop.collectAsStateWithLifecycle()
                    val sellerProducts by viewModel.sellerProducts.collectAsStateWithLifecycle()
                    val allOrders by viewModel.allOrdersAdmin.collectAsStateWithLifecycle(emptyList())

                    SellerDashboardScreen(
                        shop = sellerShop,
                        sellerProducts = sellerProducts,
                        incomingOrders = allOrders,
                        onAddProductClick = { viewModel.navigateTo(Screen.SellerAddEditProduct(null)) },
                        onEditProductClick = { prod -> viewModel.navigateTo(Screen.SellerAddEditProduct(prod.id)) },
                        onDeleteProductClick = { prod -> viewModel.deleteProduct(prod) },
                        onUpdateOrderStatus = { ordId, st -> viewModel.updateOrderStatus(ordId, st) },
                        onBackToMarketplace = { viewModel.navigateToHome() }
                    )
                }

                is Screen.SellerAddEditProduct -> {
                    val existing = approvedProducts.find { it.id == screen.productId }
                    AddEditProductScreen(
                        existingProduct = existing,
                        categories = categories,
                        onSaveProduct = { title, cat, subcat, price, discount, stock, vars, desc, brand ->
                            viewModel.saveProduct(
                                existingId = screen.productId,
                                title = title,
                                category = cat,
                                subcategory = subcat,
                                price = price,
                                discountPrice = discount,
                                stock = stock,
                                variations = vars,
                                description = desc,
                                brand = brand,
                                onSuccess = {
                                    viewModel.navigateBack()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Product published to BICHKAND!")
                                    }
                                }
                            )
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.SellerRegistration -> {
                    SellerRegistrationScreen(
                        onRegister = { fullName, shopName, email, phone, password, desc, addr, city ->
                            viewModel.registerSeller(fullName, shopName, email, phone, password, desc, addr, city) { success, msg ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                                if (success) {
                                    viewModel.navigateTo(Screen.SellerDashboard)
                                }
                            }
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.AdminDashboard -> {
                    val totalUsers by viewModel.totalUsers.collectAsStateWithLifecycle(0)
                    val totalShops by viewModel.totalShops.collectAsStateWithLifecycle(0)
                    val totalProducts by viewModel.totalProducts.collectAsStateWithLifecycle(0)
                    val totalOrders by viewModel.totalOrders.collectAsStateWithLifecycle(0)
                    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle(0.0)
                    val allShops by viewModel.allShops.collectAsStateWithLifecycle(emptyList())
                    val allProductsAdmin by viewModel.allProductsAdmin.collectAsStateWithLifecycle(emptyList())
                    val allOrdersAdmin by viewModel.allOrdersAdmin.collectAsStateWithLifecycle(emptyList())
                    val allComplaints by viewModel.allComplaints.collectAsStateWithLifecycle(emptyList())
                    val platformSettings by viewModel.platformSettings.collectAsStateWithLifecycle(null)

                    AdminDashboardScreen(
                        totalUsers = totalUsers,
                        totalShops = totalShops,
                        totalProducts = totalProducts,
                        totalOrders = totalOrders,
                        totalRevenue = totalRevenue,
                        allShops = allShops,
                        allProducts = allProductsAdmin,
                        allOrders = allOrdersAdmin,
                        categories = categories,
                        complaints = allComplaints,
                        platformSettings = platformSettings,
                        onApproveShop = { shopId -> viewModel.approveShop(shopId) },
                        onRejectShop = { shopId -> viewModel.rejectShop(shopId) },
                        onSetProductStatus = { prod, st -> viewModel.setProductStatus(prod, st) },
                        onAddCategory = { name, icon -> viewModel.addCategory(name, icon) },
                        onDeleteCategory = { cat -> viewModel.deleteCategory(cat) },
                        onUpdateComplaint = { id, st, notes -> viewModel.updateComplaint(id, st, notes) },
                        onUpdatePlatformSettings = { comm, del, ann -> viewModel.updateCommissionRate(comm, del, ann) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.ShopDetail -> {
                    val shop = approvedShops.find { it.id == screen.shopId }
                    val shopProds = approvedProducts.filter { it.shopId == screen.shopId }
                    ShopDetailScreen(
                        shop = shop,
                        shopProducts = shopProds,
                        onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                        onAddToCart = { prod ->
                            viewModel.addToCart(prod)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Added to cart!")
                            }
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }

    // City Selector Bottom Sheet
    if (showCitySelector) {
        CitySelectorBottomSheet(
            selectedCity = selectedCity,
            onCitySelected = { city -> viewModel.setCity(city) },
            onDismiss = { showCitySelector = false }
        )
    }

    // Role Switcher Bottom Sheet
    if (showRoleSwitcher) {
        RoleSwitcherBottomSheet(
            currentUser = currentUser,
            onRoleSelected = { role ->
                viewModel.switchRole(role)
                when (role) {
                    "SELLER" -> viewModel.navigateTo(Screen.SellerDashboard)
                    "ADMIN" -> viewModel.navigateTo(Screen.AdminDashboard)
                    else -> viewModel.navigateToHome()
                }
            },
            onRegisterSellerClick = {
                viewModel.navigateTo(Screen.SellerRegistration)
            },
            onDismiss = { showRoleSwitcher = false }
        )
    }
}
