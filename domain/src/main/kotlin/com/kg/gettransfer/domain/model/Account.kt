package com.kg.gettransfer.domain.model

import java.util.Locale

data class Account(
    var user: User,
    var locale: Locale,
    var currency: Currency,
    var distanceUnit: DistanceUnit,
    var groups: List<String>,
    var carrierId: Long?
) {

    var isEmailNotificationsEnabled: Boolean
        get() = groups.contains(GROUP_EMAIL_NOTIFICATION_PASSENGER)
        set(value) {
            groups = groups.toMutableList()
                    .apply {
                        if (value) add(GROUP_EMAIL_NOTIFICATION_PASSENGER)
                        else remove(GROUP_EMAIL_NOTIFICATION_PASSENGER)
                    }
        }

    companion object {
        const val GROUP_CARRIER_DRIVER = "carrier/driver"
        const val GROUP_MANAGER_VIEW_TRANSFERS = "manager/view_transfers"
        const val GROUP_EMAIL_NOTIFICATION_PASSENGER = "email_notifications/passenger"
    }
}
