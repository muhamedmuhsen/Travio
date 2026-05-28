package com.dev.hotel.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.HotelDetails
import com.example.feature.hotel.R

@Composable
fun ContactSection(
    hotelDetails: HotelDetails,
    modifier: Modifier = Modifier
) {
    val hasPhones = hotelDetails.phones.isNotEmpty()
    val hasEmail = !hotelDetails.email.isNullOrBlank()
    val hasWeb = !hotelDetails.web.isNullOrBlank()

    if (!hasPhones && !hasEmail && !hasWeb) return

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.md)
    ) {
        Text(
            text = stringResource(R.string.hotel_details_contact),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

        if (hasPhones) {
            hotelDetails.phones.forEach { phone ->
                if (!phone.number.isNullOrBlank()) {
                    ContactItem(
                        icon = Icons.Default.Phone,
                        text = phone.number!!,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.number}"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        if (hasEmail) {
            ContactItem(
                icon = Icons.Default.Email,
                text = hotelDetails.email!!,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${hotelDetails.email}"))
                    context.startActivity(intent)
                }
            )
        }

        if (hasWeb) {
            ContactItem(
                icon = Icons.Default.Language,
                text = hotelDetails.web!!,
                onClick = {
                    var url = hotelDetails.web!!
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://$url"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MaterialTheme.spacing.lg)
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
