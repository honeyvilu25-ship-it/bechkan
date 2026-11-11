package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AddressEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.CouponEntity
import com.example.ui.components.formatPkr
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItemEntity>,
    subtotal: Double,
    appliedCoupon: CouponEntity?,
    couponDiscount: Double,
    couponError: String?,
    onUpdateQuantity: (CartItemEntity, Int) -> Unit,
    onRemoveItem: (CartItemEntity) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onRemoveCoupon: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    var couponInput by remember { mutableStateOf("") }
    val deliveryFee = if (subtotal > 2500 || subtotal == 0.0) 0.0 else 199.0
    val finalTotal = (subtotal + deliveryFee - couponDiscount).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart (${cartItems.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = BichkandSurfaceLight,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Amount", fontSize = 12.sp, color = BichkandTextSecondary)
                                Text(
                                    text = formatPkr(finalTotal),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BichkandElectricBlue
                                )
                            }
                            Button(
                                onClick = onProceedToCheckout,
                                colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("proceed_to_checkout_button")
                            ) {
                                Text("Checkout (${cartItems.sumOf { it.quantity }})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RemoveShoppingCart,
                    contentDescription = null,
                    tint = BichkandTextSecondary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your shopping cart is empty", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BichkandNavyDark)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Discover the latest smartphones, traditional Pakistani fashion, and electronics today.",
                    fontSize = 12.sp,
                    color = BichkandTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onContinueShopping,
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Start Shopping")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BichkandBgLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Free delivery progress note
                item {
                    Surface(
                        color = if (subtotal >= 2500) BichkandGreen.copy(alpha = 0.12f) else BichkandLightBlueAccent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = if (subtotal >= 2500) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = if (subtotal >= 2500) BichkandGreen else BichkandElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (subtotal >= 2500) {
                                    "🎉 Congratulations! You unlocked FREE Nationwide Delivery!"
                                } else {
                                    "Add ${formatPkr(2500 - subtotal)} more to unlock FREE Delivery!"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (subtotal >= 2500) BichkandGreen else BichkandNavyDark
                            )
                        }
                    }
                }

                // Cart Items
                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cart_item_${item.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Shop Header
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = BichkandElectricBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(item.shopName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BichkandNavyDark)
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = BichkandNavyDark.copy(alpha = 0.4f), modifier = Modifier.size(30.dp))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Variation: ${item.selectedVariation}",
                                        fontSize = 11.sp,
                                        color = BichkandTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatPkr(item.price),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BichkandNavyDark
                                    )
                                }

                                // Quantity Controls
                                Column(horizontalAlignment = Alignment.End) {
                                    IconButton(
                                        onClick = { onRemoveItem(item) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BichkandRose, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                            .padding(2.dp)
                                    ) {
                                        IconButton(
                                            onClick = { onUpdateQuantity(item, item.quantity - 1) },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(14.dp))
                                        }
                                        Text(
                                            text = "${item.quantity}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )
                                        IconButton(
                                            onClick = { onUpdateQuantity(item, item.quantity + 1) },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon Code Section
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Discount Coupon", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (appliedCoupon != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BichkandGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BichkandGreen, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${appliedCoupon.code} applied (-${formatPkr(couponDiscount)})", color = BichkandGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Text(
                                        text = "Remove",
                                        color = BichkandRose,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.clickable { onRemoveCoupon() }
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it },
                                        placeholder = { Text("Enter coupon (e.g. AZADI70, BICHKAND)", fontSize = 12.sp) },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("coupon_input_field"),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onApplyCoupon(couponInput) },
                                        colors = ButtonDefaults.buttonColors(containerColor = BichkandNavyDark),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .testTag("apply_coupon_button")
                                    ) {
                                        Text("Apply", fontSize = 12.sp)
                                    }
                                }
                                if (couponError != null) {
                                    Text(
                                        text = couponError,
                                        color = BichkandRose,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Order Price Breakdown Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            SummaryRow(label = "Subtotal", value = formatPkr(subtotal))
                            SummaryRow(
                                label = "Nationwide Delivery",
                                value = if (deliveryFee == 0.0) "FREE" else formatPkr(deliveryFee),
                                isHighlight = deliveryFee == 0.0
                            )
                            if (couponDiscount > 0.0) {
                                SummaryRow(label = "Coupon Discount", value = "-${formatPkr(couponDiscount)}", isDiscount = true)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Final Total", fontWeight = FontWeight.Black, fontSize = 15.sp, color = BichkandNavyDark)
                                Text(formatPkr(finalTotal), fontWeight = FontWeight.Black, fontSize = 16.sp, color = BichkandElectricBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isHighlight: Boolean = false, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = BichkandTextSecondary)
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = if (isHighlight || isDiscount) FontWeight.Bold else FontWeight.Medium,
            color = when {
                isHighlight -> BichkandGreen
                isDiscount -> BichkandRose
                else -> BichkandTextPrimary
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    addresses: List<AddressEntity>,
    cartItems: List<CartItemEntity>,
    subtotal: Double,
    couponDiscount: Double,
    couponCode: String?,
    onAddAddress: (String, String, String, String, String, String, Boolean) -> Unit,
    onPlaceOrder: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedAddressIndex by remember { mutableStateOf(0) }
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    val deliveryFee = if (subtotal > 2500) 0.0 else 199.0
    val totalAmount = (subtotal + deliveryFee - couponDiscount).coerceAtLeast(0.0)

    val currentAddress = addresses.getOrNull(selectedAddressIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Place Order", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        },
        bottomBar = {
            Surface(
                color = BichkandSurfaceLight,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Payable", fontSize = 12.sp, color = BichkandTextSecondary)
                            Text(formatPkr(totalAmount), fontSize = 20.sp, fontWeight = FontWeight.Black, color = BichkandElectricBlue)
                        }

                        Button(
                            onClick = {
                                val addressStr = currentAddress?.let { "${it.streetAddress}, ${it.city}, ${it.province}" }
                                    ?: "House 14-B, Sector J, DHA Phase 5, Lahore"
                                val cityStr = currentAddress?.city ?: "Lahore"
                                onPlaceOrder(addressStr, cityStr, selectedPaymentMethod)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("place_order_button")
                        ) {
                            Text("Confirm & Place Order", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BichkandBgLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Delivery Address Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = BichkandElectricBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            TextButton(onClick = { showAddAddressDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Add New", fontSize = 12.sp)
                            }
                        }

                        if (addresses.isEmpty()) {
                            Text("No saved address. Click Add New above.", fontSize = 12.sp, color = BichkandTextSecondary)
                        } else {
                            addresses.forEachIndexed { index, addr ->
                                val isSelected = selectedAddressIndex == index
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedAddressIndex = index }
                                        .background(
                                            if (isSelected) BichkandLightBlueAccent else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedAddressIndex = index },
                                        colors = RadioButtonDefaults.colors(selectedColor = BichkandElectricBlue)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(addr.recipientName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(addr.label, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                        Text(addr.phone, fontSize = 11.sp, color = BichkandTextSecondary)
                                        Text("${addr.streetAddress}, ${addr.city}, ${addr.province}", fontSize = 12.sp, color = BichkandTextPrimary)
                                    }
                                }
                                if (index < addresses.size - 1) Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }

            // 2. Payment Method Card (COD, Easypaisa, JazzCash, Bank Transfer)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = BichkandElectricBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Cash on Delivery
                        PaymentMethodRow(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay with cash when your parcel is delivered to your doorstep.",
                            badge = "Most Popular in Pakistan",
                            badgeColor = BichkandGreen,
                            selected = selectedPaymentMethod == "COD",
                            onClick = { selectedPaymentMethod = "COD" }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Easypaisa
                        PaymentMethodRow(
                            title = "Easypaisa Mobile Wallet",
                            subtitle = "Pay securely via your Telenor Easypaisa account (Sandbox mode ready).",
                            badge = "Instant Wallet",
                            badgeColor = Color(0xFF00A859),
                            selected = selectedPaymentMethod == "EASYPAISA",
                            onClick = { selectedPaymentMethod = "EASYPAISA" }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // JazzCash
                        PaymentMethodRow(
                            title = "JazzCash Mobile Account",
                            subtitle = "Direct debit from your JazzCash registered mobile number (Sandbox mode ready).",
                            badge = "Instant Wallet",
                            badgeColor = Color(0xFFED1C24),
                            selected = selectedPaymentMethod == "JAZZCASH",
                            onClick = { selectedPaymentMethod = "JAZZCASH" }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bank Transfer
                        PaymentMethodRow(
                            title = "Direct Bank Transfer / Raast (1Link)",
                            subtitle = "Transfer directly to BICHKAND official HBL / Meezan corporate IBAN.",
                            badge = "Raast Instant",
                            badgeColor = BichkandElectricBlue,
                            selected = selectedPaymentMethod == "BANK_TRANSFER",
                            onClick = { selectedPaymentMethod = "BANK_TRANSFER" }
                        )
                    }
                }
            }

            // 3. Order Items Summary
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Items in Order (${cartItems.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("Qty: ${item.quantity} • ${item.selectedVariation}", fontSize = 10.sp, color = BichkandTextSecondary)
                                }
                                Text(formatPkr(item.price * item.quantity), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryRow("Subtotal", formatPkr(subtotal))
                        SummaryRow("Delivery Charges", if (deliveryFee == 0.0) "FREE" else formatPkr(deliveryFee))
                        if (couponDiscount > 0.0) {
                            SummaryRow("Coupon (${couponCode ?: ""})", "-${formatPkr(couponDiscount)}", isDiscount = true)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(formatPkr(totalAmount), fontWeight = FontWeight.Black, fontSize = 16.sp, color = BichkandElectricBlue)
                        }
                    }
                }
            }
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        var recipientName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+92 3") }
        var street by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("Lahore") }
        var province by remember { mutableStateOf("Punjab") }
        var label by remember { mutableStateOf("Home") }

        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Recipient Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number (+92)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("House / Street / Area") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City (e.g. Karachi, Lahore)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = province,
                        onValueChange = { province = it },
                        label = { Text("Province") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (recipientName.isNotBlank() && street.isNotBlank()) {
                            onAddAddress(recipientName, phone, street, city, province, label, true)
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) BichkandLightBlueAccent else Color(0xFFF8FAFC)
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(1.5.dp, BichkandElectricBlue) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = BichkandElectricBlue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BichkandNavyDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = badgeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(badge, color = badgeColor, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 10.sp, color = BichkandTextSecondary)
            }
        }
    }
}
