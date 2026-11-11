package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.*

val PakistanCities = listOf(
    "All Pakistan",
    "Karachi",
    "Lahore",
    "Islamabad",
    "Rawalpindi",
    "Faisalabad",
    "Sialkot",
    "Peshawar",
    "Multan",
    "Quetta",
    "Gujranwala",
    "Hyderabad"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectorBottomSheet(
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BichkandSurfaceLight,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BichkandElectricBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Delivery City",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BichkandNavyDark
                )
            }
            Text(
                text = "Personalize available products, delivery times, and local sellers in Pakistan.",
                fontSize = 12.sp,
                color = BichkandTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                items(PakistanCities) { city ->
                    val isSelected = city == selectedCity
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCitySelected(city)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onCitySelected(city)
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = BichkandElectricBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = city,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BichkandElectricBlue else BichkandTextPrimary
                            )
                        }
                        if (city == "All Pakistan") {
                            Surface(
                                color = BichkandLightBlueAccent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Nationwide",
                                    color = BichkandElectricBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitcherBottomSheet(
    currentUser: UserEntity?,
    onRoleSelected: (String) -> Unit,
    onRegisterSellerClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BichkandSurfaceLight,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.SwitchAccount,
                    contentDescription = null,
                    tint = BichkandElectricBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BICHKAND Role Switcher",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BichkandNavyDark
                )
            }
            Text(
                text = "Seamlessly switch between Marketplace Buyer, Seller Shop, and Platform Admin.",
                fontSize = 12.sp,
                color = BichkandTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Option 1: Buyer
            RoleItemCard(
                title = "Buyer Dashboard",
                subtitle = "Ahmed Ali Khan (Lahore) — Browse, Cart, Checkout, Reviews",
                badge = "BUYER",
                badgeColor = BichkandElectricBlue,
                isSelected = currentUser?.role == "BUYER",
                icon = Icons.Default.ShoppingBag,
                onClick = {
                    onRoleSelected("BUYER")
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 2: Seller
            RoleItemCard(
                title = "Seller Dashboard",
                subtitle = "Zain Ul Abideen (TechHub Saddar) — Manage Stock, Orders, Sales",
                badge = "SELLER",
                badgeColor = BichkandAmber,
                isSelected = currentUser?.role == "SELLER",
                icon = Icons.Default.Storefront,
                onClick = {
                    onRoleSelected("SELLER")
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 3: Admin
            RoleItemCard(
                title = "Admin Panel",
                subtitle = "Marketplace Superadmin — Approvals, Categories, Platform Stats",
                badge = "ADMIN",
                badgeColor = BichkandRose,
                isSelected = currentUser?.role == "ADMIN",
                icon = Icons.Default.AdminPanelSettings,
                onClick = {
                    onRoleSelected("ADMIN")
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Register new seller button
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onRegisterSellerClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_seller_registration_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BichkandNavyDark)
            ) {
                Icon(
                    imageVector = Icons.Default.AddBusiness,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open New Seller Shop in Pakistan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun RoleItemCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BichkandLightBlueAccent else Color(0xFFF8FAFC)
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(BichkandElectricBlue)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BichkandNavyDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = badgeColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badge,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = BichkandTextSecondary
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = BichkandElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
