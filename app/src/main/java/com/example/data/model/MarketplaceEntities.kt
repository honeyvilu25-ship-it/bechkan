package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val passwordHash: String = "123456",
    val name: String,
    val phone: String,
    val role: String, // "BUYER", "SELLER", "ADMIN"
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "seller_shops")
data class SellerShopEntity(
    @PrimaryKey val id: String,
    val sellerId: String,
    val shopName: String,
    val description: String,
    val phone: String,
    val businessAddress: String,
    val city: String,
    val logoUrl: String = "",
    val status: String = "APPROVED", // "APPROVED", "PENDING", "REJECTED"
    val commissionRate: Double = 0.05,
    val rating: Double = 4.8,
    val totalSales: Int = 0,
    val earnings: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val imageUrl: String = "",
    val productCount: Int = 0
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val shopId: String,
    val shopName: String,
    val title: String,
    val description: String,
    val category: String,
    val subcategory: String = "",
    val price: Double, // in PKR
    val discountPrice: Double? = null,
    val stock: Int = 10,
    val variationsJson: String = "[]", // e.g. ["Standard", "Large"] or ["Black", "Silver"]
    val imageUrlsJson: String = "[]",
    val rating: Double = 4.5,
    val reviewCount: Int = 0,
    val status: String = "APPROVED", // "APPROVED", "PENDING", "SUSPENDED"
    val isFlashDeal: Boolean = false,
    val isFeatured: Boolean = false,
    val brand: String = "Generic",
    val cityLocation: String = "Karachi",
    val createdAt: Long = System.currentTimeMillis()
) {
    val displayPrice: Double get() = discountPrice ?: price
    val discountPercentage: Int get() {
        if (discountPrice != null && discountPrice < price) {
            return (((price - discountPrice) / price) * 100).toInt()
        }
        return 0
    }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String,
    val title: String,
    val price: Double,
    val imageUrl: String,
    val shopName: String,
    val quantity: Int = 1,
    val selectedVariation: String = "Standard"
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String,
    val title: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val deliveryAddress: String,
    val city: String,
    val paymentMethod: String, // "COD", "EASYPAISA", "JAZZCASH", "BANK_TRANSFER"
    val paymentStatus: String = "PENDING", // "PENDING", "PAID"
    val orderStatus: String = "PENDING", // "PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED", "RETURNED"
    val subtotal: Double,
    val deliveryFee: Double = 199.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val couponCode: String? = null,
    val itemsJson: String, // serialized list of OrderItemData
    val sellerIdsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class OrderItemData(
    val productId: String,
    val shopId: String,
    val shopName: String,
    val title: String,
    val price: Double,
    val quantity: Int,
    val variation: String,
    val imageUrl: String
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val label: String, // "Home", "Office", "Other"
    val recipientName: String,
    val phone: String,
    val streetAddress: String,
    val city: String,
    val province: String = "Punjab",
    val isDefault: Boolean = false
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val id: String,
    val code: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minOrder: Double,
    val active: Boolean = true
)

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val userId: String,
    val userName: String,
    val subject: String,
    val description: String,
    val status: String = "OPEN", // "OPEN", "INVESTIGATING", "RESOLVED"
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_settings")
data class PlatformSettingsEntity(
    @PrimaryKey val id: String = "default",
    val commissionPercent: Double = 5.0,
    val baseDeliveryFee: Double = 199.0,
    val announcementText: String = "Welcome to BICHKAND! Free delivery across Pakistan on orders over Rs. 2,500!"
)
