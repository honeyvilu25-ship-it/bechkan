package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCountFlow(): Flow<Int>
}

@Dao
interface ShopDao {
    @Query("SELECT * FROM seller_shops WHERE id = :id")
    suspend fun getShopById(id: String): SellerShopEntity?

    @Query("SELECT * FROM seller_shops WHERE sellerId = :sellerId LIMIT 1")
    suspend fun getShopBySellerId(sellerId: String): SellerShopEntity?

    @Query("SELECT * FROM seller_shops WHERE sellerId = :sellerId LIMIT 1")
    fun getShopBySellerIdFlow(sellerId: String): Flow<SellerShopEntity?>

    @Query("SELECT * FROM seller_shops ORDER BY createdAt DESC")
    fun getAllShopsFlow(): Flow<List<SellerShopEntity>>

    @Query("SELECT * FROM seller_shops WHERE status = 'APPROVED' ORDER BY rating DESC")
    fun getApprovedShopsFlow(): Flow<List<SellerShopEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: SellerShopEntity)

    @Update
    suspend fun updateShop(shop: SellerShopEntity)

    @Query("SELECT COUNT(*) FROM seller_shops")
    fun getShopCountFlow(): Flow<Int>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE status = 'APPROVED' ORDER BY createdAt DESC")
    fun getAllApprovedProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProductsAdminFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getProductsByShopFlow(shopId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductByIdFlow(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE category = :category AND status = 'APPROVED' ORDER BY createdAt DESC")
    fun getProductsByCategoryFlow(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isFlashDeal = 1 AND status = 'APPROVED' ORDER BY createdAt DESC")
    fun getFlashDealsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isFeatured = 1 AND status = 'APPROVED' ORDER BY rating DESC")
    fun getFeaturedProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'APPROVED' AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%')")
    fun searchProductsFlow(query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT COUNT(*) FROM products")
    fun getProductCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItemsFlow(userId: String): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items WHERE userId = :userId")
    fun getWishlistFlow(userId: String): Flow<List<WishlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeWishlistItem(userId: String, productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE userId = :userId AND productId = :productId)")
    fun isInWishlistFlow(userId: String, productId: String): Flow<Boolean>
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    fun getOrdersByBuyerFlow(buyerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE id = :id")
    fun getOrderByIdFlow(id: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT COUNT(*) FROM orders")
    fun getOrderCountFlow(): Flow<Int>

    @Query("SELECT SUM(totalAmount) FROM orders WHERE paymentStatus = 'PAID' OR orderStatus != 'CANCELLED'")
    fun getTotalRevenueFlow(): Flow<Double?>
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
    fun getReviewsForProductFlow(productId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC")
    fun getAddressesFlow(userId: String): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaults(userId: String)
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons WHERE active = 1")
    fun getActiveCouponsFlow(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE code = :code AND active = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)
}

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaintsFlow(): Flow<List<ComplaintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity)

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)
}

@Dao
interface PlatformDao {
    @Query("SELECT * FROM platform_settings WHERE id = 'default'")
    fun getSettingsFlow(): Flow<PlatformSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: PlatformSettingsEntity)
}
