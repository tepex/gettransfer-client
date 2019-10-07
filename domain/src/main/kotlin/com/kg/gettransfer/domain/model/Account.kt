package com.kg.gettransfer.domain.model

import java.util.Locale

data class Account(
    var user: User,
    var locale: Locale,
    var currency: Currency,
    var distanceUnit: DistanceUnit,
    var groups: List<String>,
    var carrierId: Long?,
    var partner: Partner?
) {

    var isEmailNotificationsEnabled: Boolean
        get() = groups.contains(GROUP_EMAIL_NOTIFICATION_PASSENGER)
        set(value) {
            groups = groups.toMutableList().apply {
                if (value) add(GROUP_EMAIL_NOTIFICATION_PASSENGER) else remove(GROUP_EMAIL_NOTIFICATION_PASSENGER)
            }
        }

    val isCarrier: Boolean
        get() = groups.find { it.contains(GROUP_CARRIER) } != null

    val isManager: Boolean
        get() = groups.indexOf(GROUP_MANAGER_VIEW_TRANSFERS) >= 0

    val isBusinessAccount: Boolean
        get() = partner != null

    companion object {
        const val GROUP_CARRIER = "carrier"
        const val GROUP_MANAGER_VIEW_TRANSFERS = "manager/view_transfers"
        const val GROUP_EMAIL_NOTIFICATION_PASSENGER = "email_notifications/passenger"

        val EMPTY = Account(
            user = User.EMPTY,
            locale = Locale.getDefault(),
            currency = Currency.DEFAULT,
            distanceUnit = DistanceUnit.KM,
            groups = emptyList(),
            carrierId = null,
            partner = null
        )
    }
}
