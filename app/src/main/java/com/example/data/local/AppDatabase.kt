package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SellerShopEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        ReviewEntity::class,
        AddressEntity::class,
        CouponEntity::class,
        ComplaintEntity::class,
        PlatformSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun shopDao(): ShopDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun addressDao(): AddressDao
    abstract fun couponDao(): CouponDao
    abstract fun complaintDao(): ComplaintDao
    abstract fun platformDao(): PlatformDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bichkand_marketplace.db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    DatabaseSeeder.seedDatabase(database)
                }
            }
        }
    }
}
